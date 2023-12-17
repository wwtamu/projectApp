package com.wsw.oauth.model;

import java.util.Date;

public interface RevokedToken {

    public String getTokenValue();

    public void setTokenValue(String tokenValue);
    
    public String getClientId();
	
	public void setClientId(String clientId);
    
    public boolean getExpires();

    public void setExpires(boolean expires);

    public Date getExpiration();

    public void setExpiration(Date expiration);
    
}