package com.wsw.api.config;

import java.io.IOException;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import javax.annotation.Resource;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import javax.servlet.ServletException;
import javax.servlet.FilterChain;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationManager;
import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;
import org.springframework.web.filter.OncePerRequestFilter;


import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;

import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;

@Configuration
@EnableResourceServer
@PropertySource("classpath:application.properties")
public class OAuth2Config extends ResourceServerConfigurerAdapter {
	
	private static final String REMOTE_AUTH_SERVER = "spring.remote_oauth_server";
	
	private static final String REMOTE_CLIENT_ID = "spring.remote_client_id";
	
	private static final String REMOTE_CLIENT_SECRET = "spring.remote_client_secret";
	
	@Resource
	private Environment env;
	
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
        tokenEnhancerChain.setTokenEnhancers(enhancers);
        return tokenEnhancerChain;
    }
	
    @Bean
    public ResourceServerTokenServices tokenServices() {
        final RemoteTokenServices tokenServices = new RemoteTokenServices();
        tokenServices.setClientId(env.getRequiredProperty(REMOTE_CLIENT_ID));
        tokenServices.setClientSecret(env.getRequiredProperty(REMOTE_CLIENT_SECRET));
        tokenServices.setCheckTokenEndpointUrl(env.getRequiredProperty(REMOTE_AUTH_SERVER));
        return tokenServices;
    }
    
    @Bean
    public AuthenticationManager authenticationManager() {
        final OAuth2AuthenticationManager oAuth2AuthenticationManager = new OAuth2AuthenticationManager();
        oAuth2AuthenticationManager.setTokenServices(tokenServices());
        return oAuth2AuthenticationManager;
    }
    
    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {        
        resources.resourceId("stomp-services");
        resources.tokenStore(tokenStore());
        resources.tokenServices(tokenServices());         
        resources.authenticationManager(authenticationManager());
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.addFilterAfter(checkHeaders(), AbstractPreAuthenticatedProcessingFilter.class)
        	.authorizeRequests()
                .antMatchers(HttpMethod.GET, "/**").access("#oauth2.hasScope('read')")
                .antMatchers(HttpMethod.POST, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PATCH, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.PUT, "/**").access("#oauth2.hasScope('write')")
                .antMatchers(HttpMethod.DELETE, "/**").access("#oauth2.hasScope('write')")
            .and()
                .headers().addHeaderWriter((request, response) -> {
                    if (request.getMethod().equals("OPTIONS")) {
                    	response.setHeader("Access-Control-Allow-Credentials", request.getHeader("Access-Control-Allow-Credentials"));
                    	response.setHeader("Access-Control-Allow-Origin", request.getHeader("Access-Control-Allow-Origin"));
                        response.setHeader("Access-Control-Allow-Methods", request.getHeader("Access-Control-Request-Method"));
                        response.setHeader("Access-Control-Allow-Headers", request.getHeader("Access-Control-Request-Headers"));
                    }
                });    
    }

    private OncePerRequestFilter checkHeaders() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
            	
            	System.out.println("\n\n REQUEST HEADERS \n");
            	Enumeration<String> headerNames = request.getHeaderNames();
        		while(headerNames.hasMoreElements()) {
        			String headerName = headerNames.nextElement();
        			System.out.println(headerName + ": " + request.getHeader(headerName));
        		}
        		System.out.println("\n\n");
            	
                filterChain.doFilter(request, response);
            }
        };
    }

}