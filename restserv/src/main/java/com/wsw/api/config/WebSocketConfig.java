  package com.wsw.api.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.AbstractWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;

import com.wsw.api.controller.interceptor.StompInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractWebSocketMessageBrokerConfigurer {
	
	@Autowired
	public StompInterceptor stompInterceptor;
	
	@Autowired
	public HttpSessionIdHandshakeInterceptor handshakeInterceptor;
	
	@Bean
	public StompInterceptor configureStompInterceptor() {
		return new StompInterceptor();
	}
	
	@Bean
	public HttpSessionIdHandshakeInterceptor configureHandshakeInterceptor() {
		return new HttpSessionIdHandshakeInterceptor();
	}
	
	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
		registry.enableSimpleBroker("/queue", "/channel");
		registry.setApplicationDestinationPrefixes("/ws");
		registry.setUserDestinationPrefix("/private");
	}
	
	@Override
	public void registerStompEndpoints(StompEndpointRegistry registry) {
		registry.addEndpoint("/connect")
				.withSockJS()
				.setInterceptors(handshakeInterceptor);
	}
	
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(stompInterceptor);
	}
	
}
