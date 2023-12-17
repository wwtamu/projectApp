package com.wsw.oauth.controllers;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.LinkedHashMap;
import java.util.Date;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.DefaultOAuth2RefreshToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.ClientAlreadyExistsException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.TokenRequest;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.approval.Approval;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.common.ExpiringOAuth2RefreshToken;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.wsw.oauth.CustomTokenGranter;
import com.wsw.oauth.model.OAuthClientDetails;
import com.wsw.oauth.model.impl.AuthResponseImpl;
import com.wsw.oauth.model.repo.RevokedTokenRepo;
import com.wsw.oauth.model.impl.RevokedTokenImpl;

@RestController
public class AuthController {
	
	private static final String DEFAULT_ROLE = "ROLE_USER";
	private static final String GRANT_TYPE = "refresh_token";
	private static final String TOKEN_ID = "jti";
	
	private static final int ACCESS_TOKEN_VALIDITY = 15;
	private static final int REFRESH_TOKEN_VALIDITY = 60;
	
	@Autowired 
	private PasswordEncoder passwordEncoder;
	
	@Autowired 
	private JdbcClientDetailsService clientDetailsService;

	@Autowired
	private AuthorizationServerTokenServices tokenServices;
	
	@Autowired
	private DefaultOAuth2RequestFactory requestFactory;
	
	@Autowired
	private CustomTokenGranter tokenGranter;
	
	@Autowired
	private JwtTokenStore tokenStore;
	
	@Autowired
	private RevokedTokenRepo revokedTokenRepo;
	
	@RequestMapping(value = "/register", method = RequestMethod.GET)
    public AuthResponseImpl register(@RequestHeader() Map<String,String> headers) {

		String clientId        = decode(headers.get("client-id"));
    	String clientFirstName = decode(headers.get("client-firstname"));
    	String clientLastName  = decode(headers.get("client-lastname"));
    	String clientEmail     = decode(headers.get("client-email"));
    	String clientSecret    = decode(headers.get("client-secret"));
    	String clientConfirm   = decode(headers.get("client-confirm"));
    	
    	if(!clientSecret.equals(clientConfirm)) {
    		throw new BadRequestException();
    	}
    	    	
    	String additionalInformation = "id:" + clientId + "," + 
    								   "first_name:" + clientFirstName + "," + 
    								   "last_name:" + clientLastName + "," + 
    								   "email:" + clientEmail + "," +
    								   "role:" + DEFAULT_ROLE;
    	;
    	ClientDetails clientDetails = new OAuthClientDetails(clientId, 
    														 "stomp-services", 
    														 passwordEncoder.encode(clientSecret), 
    														 "read,write,trust", 
    														 GRANT_TYPE, 
    														 "", 
    														 DEFAULT_ROLE, 
    														 ACCESS_TOKEN_VALIDITY, 
    														 REFRESH_TOKEN_VALIDITY, 
    														 additionalInformation, 
    														 "false");
    	 
    	clientDetailsService.addClientDetails(clientDetails);
    	
    	ClientDetails registeredClient = clientDetailsService.loadClientByClientId(clientId);
    	
    	if(!passwordEncoder.matches(clientSecret, registeredClient.getClientSecret())) {
    		throw new UnauthorizedException();
    	}
    	
    	Map<String,String> parameters = new HashMap<String,String>();
    	parameters.put("client-id", clientId);
    	parameters.put("client-secret", clientSecret);
    	
    	TokenRequest tokenRequest = requestFactory.createTokenRequest(parameters, registeredClient);
    	
    	OAuth2AccessToken accessToken = tokenGranter.grant(GRANT_TYPE, tokenRequest);

    	Map<String, Object> additionalInformationMap = accessToken.getAdditionalInformation();
    	
    	additionalInformationMap.put("credentials", registeredClient.getAdditionalInformation());
    	
    	((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformationMap);
    	
        return new AuthResponseImpl("SUCCESS", accessToken, "Client registered.");    	
	}

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public AuthResponseImpl login(@RequestHeader() Map<String,String> headers) {
    	
    	String clientId = decode(headers.get("client-id"));
    	String clientSecret = decode(headers.get("client-secret"));
    	String clientRemember = decode(headers.get("client-remember"));
    	
    	ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);
    	
    	if(!passwordEncoder.matches(clientSecret, authenticatedClient.getClientSecret())) {
    		throw new UnauthorizedException();
    	}
    	
    	Map<String,String> parameters = new HashMap<String,String>();
    	parameters.put("client-id", clientId);
    	parameters.put("client-secret", clientSecret);
    	
    	TokenRequest tokenRequest = requestFactory.createTokenRequest(parameters, authenticatedClient);
    	
    	OAuth2AccessToken accessToken = tokenGranter.grant(GRANT_TYPE, tokenRequest);
    	
    	OAuth2RefreshToken refreshToken = null;
    	    	
    	if(clientRemember.equals("true")) {    		    		
    		RevokedTokenImpl nonExpiringRefreshToken = revokedTokenRepo.findByClientIdAndExpires(clientId, false);
    		if(nonExpiringRefreshToken != null) {
    			refreshToken = new DefaultOAuth2RefreshToken(nonExpiringRefreshToken.getTokenValue());
    			revokedTokenRepo.deleteByClientIdAndExpires(clientId, false);
    		}
    		else {
	    		((BaseClientDetails) authenticatedClient).setAccessTokenValiditySeconds(new Integer(0));
	    		((BaseClientDetails) authenticatedClient).setRefreshTokenValiditySeconds(new Integer(0));
	    		clientDetailsService.updateClientDetails(authenticatedClient);
	    		
	    		OAuth2AccessToken tempAccessToken = tokenGranter.grant(GRANT_TYPE, tokenRequest);
	    		
	    		refreshToken = tempAccessToken.getRefreshToken();
	    		
	    		((BaseClientDetails) authenticatedClient).setAccessTokenValiditySeconds(new Integer(ACCESS_TOKEN_VALIDITY));
	    		clientDetailsService.updateClientDetails(authenticatedClient);	    		
    		}    		    		
    		((DefaultOAuth2AccessToken) accessToken).setRefreshToken(refreshToken);    		
    	}
    	    	
    	Map<String, Object> additionalInformation = accessToken.getAdditionalInformation();
    	
    	additionalInformation.put("credentials", authenticatedClient.getAdditionalInformation());
    	
    	((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformation);
    	
        return new AuthResponseImpl("SUCCESS", accessToken);
    }
    
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public AuthResponseImpl logout(@RequestHeader() Map<String,String> headers) {		

    	String clientId = decode(headers.get("client-id"));
    	String clientRefreshToken = decode(headers.get("refresh-token"));

    	ClientDetails authenticatedClient = clientDetailsService.loadClientByClientId(clientId);
    	
    	DefaultExpiringOAuth2RefreshToken expiringRefreshToken = null;
    	
    	try {
    		expiringRefreshToken = (DefaultExpiringOAuth2RefreshToken) tokenStore.readRefreshToken(clientRefreshToken);
    		revokedTokenRepo.save(new RevokedTokenImpl(clientRefreshToken, clientId, true, expiringRefreshToken.getExpiration()));
    	}
    	catch(Exception e) {
    		revokedTokenRepo.save(new RevokedTokenImpl(clientRefreshToken, clientId, false));
    	}
    	
    	if(authenticatedClient.getRefreshTokenValiditySeconds() == 0) {
			((BaseClientDetails) authenticatedClient).setRefreshTokenValiditySeconds(new Integer(REFRESH_TOKEN_VALIDITY));
			clientDetailsService.updateClientDetails(authenticatedClient);
		}

    	return new AuthResponseImpl("SUCCESS", "Client logged out.");
	}
    
    @RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public AuthResponseImpl refresh(@RequestHeader() Map<String,String> headers) {

		String clientId = decode(headers.get("client-id"));
    	String clientRefreshToken = decode(headers.get("refresh-token"));
    	
    	ClientDetails clientDetails = clientDetailsService.loadClientByClientId(clientId);
    	
    	Map<String,String> parameters = new HashMap<String,String>();
    	parameters.put("client-id", clientId);
    	
    	Set<String> scope = new HashSet<String>();
    	scope.add("read");
    	scope.add("write");

    	OAuth2AccessToken accessToken = tokenServices.refreshAccessToken(clientRefreshToken, new TokenRequest(parameters, clientId, scope, GRANT_TYPE));
    	
    	OAuth2RefreshToken refreshToken = new DefaultOAuth2RefreshToken(clientRefreshToken);
    				
    	((DefaultOAuth2AccessToken) accessToken).setRefreshToken(refreshToken);
    	    	    	    	
    	Map<String, Object> additionalInformationMap = accessToken.getAdditionalInformation();
    	
    	additionalInformationMap.put("credentials", clientDetails.getAdditionalInformation());
    	
    	((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInformationMap);
    	
    	return new AuthResponseImpl("SUCCESS", accessToken);    	
	}
    
    public String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }
    
    @ExceptionHandler(Exception.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public @ResponseBody AuthResponseImpl handleException(Exception e) {
        return new AuthResponseImpl("FAILURE", e.getClass().getSimpleName());
    }
    
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public class BadRequestException extends RuntimeException  {
    	public BadRequestException() {}
    }
    
    @ResponseStatus(value = HttpStatus.UNAUTHORIZED)
    public class UnauthorizedException extends RuntimeException  {
    	public UnauthorizedException() {}
    }
    
}
