package com.wsw.oauth.controller.interceptor;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Collection;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.StringUtils;

import com.wsw.oauth.model.repo.RevokedTokenRepo;
import com.wsw.oauth.model.impl.RevokedTokenImpl;

@Component
public class AuthInterceptor extends HandlerInterceptorAdapter {

	@Autowired
	private RevokedTokenRepo revokedTokenRepo;
	
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
				
		String refreshToken = decode(request.getHeader("refresh-token"));
		
		if(refreshToken != null) {
			if(revokedTokenRepo.getTokenByTokenValue(refreshToken) != null) {
	    		throw new RevokedRefreshTokenException();
	    	}
		}
		
		Map<String, String[]> parameterMap = request.getParameterMap();
		
		if(parameterMap.get("token") != null) {
			if(revokedTokenRepo.getTokenByTokenValue(parameterMap.get("token")[0]) != null) {
	    		throw new RevokedRefreshTokenException();
	    	}
		}
		
        return true;
    }
	
	@ResponseStatus(value=HttpStatus.FORBIDDEN, reason="Token revoked.") 
	public class RevokedRefreshTokenException extends RuntimeException {
		private static final long serialVersionUID = 1L;
	}
	
	public String decode(String s) {
        return StringUtils.newStringUtf8(Base64.decodeBase64(s));
    }
	
}



