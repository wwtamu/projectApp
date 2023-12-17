package com.wsw.api.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Arrays;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.cloud.security.oauth2.resource.EnableOAuth2Resource;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.access.prepost.PreAuthorize;

import org.springframework.messaging.MessageHeaders;

import org.springframework.security.oauth2.provider.ClientDetails;

import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.MacSigner;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.wsw.api.model.impl.RestResponseImpl;

@EnableOAuth2Resource
@RestController
@MessageMapping("/user")
public class UserController {
		
	@Autowired
	public ObjectMapper objectMapper;
		
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	@MessageMapping("/decode_token")
	@SendToUser
	private RestResponseImpl details(Authentication authentication) {
		
		OAuth2Authentication oauth = (OAuth2Authentication) authentication; 
		
		OAuth2AuthenticationDetails details = (OAuth2AuthenticationDetails) authentication.getDetails();
		
		MacSigner hmac = new MacSigner("signing-secret");
		
		Jwt token = null;
		Map<String, String> tokenMap = null;
		try {
			token = JwtHelper.decodeAndVerify(details.getTokenValue(), hmac);				
			tokenMap = objectMapper.readValue(token.getClaims(), Map.class);
		} catch (Exception e) {
			System.out.println("Invalid token!"); 
			return new RestResponseImpl("failure", "INVALID_TOKEN");
		}
		
		System.out.println(oauth.getName());
		System.out.println(oauth.isAuthenticated());
		System.out.println(oauth.isClientOnly());
		System.out.println(oauth.getDetails());
		
		System.out.println(tokenMap);
		
		return new RestResponseImpl("success", tokenMap);
		
	}
	
	@PreAuthorize("hasRole('ROLE_USER')")
	@MessageMapping("/test")
	@SendToUser
	private RestResponseImpl test() {
		
		return new RestResponseImpl("success", "pass");
		
	}
	
}
