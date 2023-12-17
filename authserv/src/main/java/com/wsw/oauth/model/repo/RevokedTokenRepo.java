package com.wsw.oauth.model.repo;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.wsw.oauth.model.impl.RevokedTokenImpl;

@Repository
public interface RevokedTokenRepo extends JpaRepository <RevokedTokenImpl, Long>{
	
	public RevokedTokenImpl getTokenByTokenValue(String tokenValue);
	
	@Transactional
	public Long deleteByExpirationLessThan(Date expiration);
	
	@Transactional
	public Long deleteByExpiresAndExpirationLessThan(boolean expires, Date expiration);
	
	public RevokedTokenImpl findByClientIdAndExpires(String clientId, boolean expires);
	
	@Transactional
	public Long deleteByClientIdAndExpires(String clientId, boolean expires);
	
}
