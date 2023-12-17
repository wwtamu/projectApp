package com.wsw.api.controller.interceptor;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class StompInterceptor extends ChannelInterceptorAdapter {
	
	@Override 
	@SuppressWarnings("unchecked")	
	public Message<?> preSend(Message<?> message, MessageChannel channel) {
		StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
		StompCommand command = accessor.getCommand();
				
		if(accessor.getDestination() != null) {
			System.out.println(accessor.getDestination());
		}
				
		System.out.println("\n\n " + command.name() + "\n\n");
				
		if("SEND".equals(command.name())) {
			
		}
		else if("CONNECT".equals(command.name())) {
			
		}
		else if("SUBSCRIBE".equals(command.name())) {
			
		}
		else if("UNSUBSCRIBE".equals(command.name())) {
			
		}
		
		return message;
	}
	
}

