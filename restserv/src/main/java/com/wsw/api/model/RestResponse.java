package com.wsw.api.model;

import java.util.HashMap;

public abstract class RestResponse {
	
	public String response;
    public HashMap<String, Object> content;
    
	public String getResponse() {
		return response;
	}
	
	public Object getContent() {
		return content;
	}
   
}

