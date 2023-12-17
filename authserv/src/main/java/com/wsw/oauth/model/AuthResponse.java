package com.wsw.oauth.model;

import java.util.HashMap;

public abstract class AuthResponse {
	
	public String response;
    public HashMap<String, Object> content;
    
	public String getResponse() {
		return response;
	}
	
	public Object getContent() {
		return content;
	}
   
}

