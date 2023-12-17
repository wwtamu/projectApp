package com.wsw.api.model.impl;

import java.util.ArrayList;
import java.util.HashMap;

import com.wsw.api.model.RestResponse;

public class RestResponseImpl extends RestResponse {

    public RestResponseImpl(String response, Object ... objects) {
        this.response = response;
        
        HashMap<String, Object> content = new HashMap<String, Object>();
      
        for(Object obj : objects) {
        	
        	String objectType = obj.getClass().getSimpleName();
        	        	
        	if(objectType.equals("ArrayList")) {
        		ArrayList<?> a = ((ArrayList<?>) obj);
        		if(a.size()>0)
        			objectType += "<"+a.get(0).getClass().getSimpleName()+">";
        	}
        	
        	content.put(objectType, obj);
        }
        
        this.content = content;
    }

    public RestResponseImpl(String response) {
        this.response = response;
        this.content = null;
    }
	
}

