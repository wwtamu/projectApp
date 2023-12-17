package com.wsw.oauth.config;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.sql.DataSource;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.approval.TokenApprovalStore;
import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
import org.springframework.security.oauth2.provider.code.AuthorizationCodeServices;
import org.springframework.security.oauth2.provider.code.JdbcAuthorizationCodeServices;
import org.springframework.security.oauth2.provider.request.DefaultOAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.approval.UserApprovalHandler;
import org.springframework.security.oauth2.provider.approval.TokenStoreUserApprovalHandler;
import org.springframework.security.oauth2.provider.CompositeTokenGranter;
import org.springframework.security.oauth2.provider.TokenGranter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.oauth2.provider.client.ClientDetailsUserDetailsService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.approval.ApprovalStore;

import com.wsw.oauth.CustomTokenGranter;
import com.wsw.oauth.controller.interceptor.AuthInterceptor;

@Configuration
@EnableAuthorizationServer
@PropertySource("classpath:application.properties")
public class OAuth2Config extends AuthorizationServerConfigurerAdapter {
	
	private static final String GRANT_TYPE = "refresh_token";
	
	private static final int REFRESH_TOKEN_VALIDITY = 60;
	
	@Autowired
	private AuthInterceptor authInterceptor; 
		
	@Bean
	@Primary
	@ConfigurationProperties(prefix = "spring.datasource")
	public DataSource oauthDataSource() {
		return DataSourceBuilder.create().build();
	}

    @Bean
    public JdbcClientDetailsService clientDetailsService() {
        return new JdbcClientDetailsService(oauthDataSource());
    }
    
    @Bean
    public JwtTokenStore tokenStore() {
    	return new JwtTokenStore(tokenEnhancer());
    }
           
    @Bean 
    public JwtAccessTokenConverter tokenEnhancer() {
		JwtAccessTokenConverter jwtAccessTokenConverter = new JwtAccessTokenConverter();
		jwtAccessTokenConverter.setSigningKey("signing-secret"); 
    	return jwtAccessTokenConverter;
    }
    
    @Bean
    public TokenEnhancerChain tokenEnhancerChain(){
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = new ArrayList<TokenEnhancer>();
        enhancers.add(tokenEnhancer());
        enhancers.add(new CustomTokenEnhancer());
        tokenEnhancerChain.setTokenEnhancers(enhancers);
        return tokenEnhancerChain;
    }
                     
    @Bean 
    public AuthorizationServerTokenServices tokenServices() { 
    	DefaultTokenServices tokenServices = new DefaultTokenServices();
    	tokenServices.setTokenEnhancer(tokenEnhancerChain());
    	tokenServices.setTokenStore(tokenStore());
    	tokenServices.setRefreshTokenValiditySeconds(REFRESH_TOKEN_VALIDITY);
    	tokenServices.setSupportRefreshToken(true);
    	tokenServices.setClientDetailsService(clientDetailsService());
    	return tokenServices; 
    }
            
    @Bean
    public DefaultOAuth2RequestFactory requestFactory() {
    	return new DefaultOAuth2RequestFactory(clientDetailsService());
    }
    
    @Bean
    public CustomTokenGranter tokenGranter() {
    	return new CustomTokenGranter(tokenServices(), clientDetailsService(), requestFactory(), GRANT_TYPE);
    }
    
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {    	
        clients.withClientDetails(clientDetailsService());
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
    	oauthServer.tokenKeyAccess("hasAuthority('ROLE_ADMIN')").checkTokenAccess("hasAuthority('ROLE_ADMIN')");
    }

    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
    	endpoints.addInterceptor(authInterceptor)
    			 .tokenServices(tokenServices())
    			 .tokenGranter(tokenGranter());
    }
        
    private static class CustomTokenEnhancer implements TokenEnhancer {
        @Override
        public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
            final DefaultOAuth2AccessToken result = new DefaultOAuth2AccessToken(accessToken);            
            result.getAdditionalInformation().put("company", "WWLLC");
            return result;
        }
    }
    
}
