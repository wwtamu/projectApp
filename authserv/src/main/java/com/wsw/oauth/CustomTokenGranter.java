package com.wsw.oauth;

import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2RequestFactory;
import org.springframework.security.oauth2.provider.token.AbstractTokenGranter;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;

public class CustomTokenGranter extends AbstractTokenGranter {

    public CustomTokenGranter(AuthorizationServerTokenServices tokenServices, 
    						  ClientDetailsService clientDetailsService, 
    						  OAuth2RequestFactory requestFactory,
    						  String grantType) {
    		super(tokenServices, clientDetailsService, requestFactory, grantType);
    }

}
