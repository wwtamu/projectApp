package com.wsw.oauth.model.impl;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.wsw.oauth.model.RevokedToken;

@Entity
@Table(name="revoked_tokens")
public class RevokedTokenImpl implements RevokedToken {
	
	@Id
	@Column(name = "token_value", length = 1000)
    private String tokenValue;
	
	@Column(name = "client_id")
    private String clientId;
	
	@Temporal(TemporalType.TIMESTAMP)
	private Date expiration;
	
	private boolean expires;
	
	public RevokedTokenImpl() {}
	        
    public RevokedTokenImpl(String tokenValue, String clientId, boolean expires) {
    	this.tokenValue = tokenValue;
    	this.clientId = clientId;
    	this.expires = expires;
    }
    
    public RevokedTokenImpl(String tokenValue, String clientId, boolean expires, Date expiration) {
    	this.tokenValue = tokenValue;
    	this.clientId = clientId;
    	this.expires = expires;
    	this.expiration = expiration;
    }
    
    @Override
    public String getTokenValue() {
    	return tokenValue;
    }
    
    @Override
    public void setTokenValue(String tokenValue) {
    	this.tokenValue = tokenValue;
    }
    
    @Override
    public String getClientId() {
    	return clientId;
    }
	
    @Override
	public void setClientId(String clientId) {
    	this.clientId = clientId;
    }
    
    @Override
    public Date getExpiration() {
    	return expiration;
    }
    
    @Override
    public void setExpiration(Date expiration) {
    	this.expiration = expiration;
    }
    
    @Override
    public boolean getExpires() {
    	return expires;
    }
    
    @Override
    public void setExpires(boolean expires) {
    	this.expires = expires;
    }
    
}