package com.wsw.oauth.service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.wsw.oauth.ApplicationContextProvider;
import com.wsw.oauth.model.repo.RevokedTokenRepo;

@Service
public class RevokedTokenManagement {
	
	@Autowired
	private RevokedTokenRepo revokedTokenRepo;
	
	public RevokedTokenManagement() {}
	
	@Scheduled(fixedDelay=60000)
	public void removeExpiredRevokedTokens() throws IOException {
		Date now = new Date();		
		System.out.println("Deleting expired revoked tokens.");
		System.out.println(revokedTokenRepo.deleteByExpiresAndExpirationLessThan(true, now) + " deleted");
	}
}

