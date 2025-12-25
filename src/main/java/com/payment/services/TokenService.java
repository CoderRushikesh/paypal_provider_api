package com.payment.services;

import org.springframework.stereotype.Service;

import com.payment.http.HttpServiceEngine;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {
	private final HttpServiceEngine httpServiceEngine;
	
	// TODO , implement caching mechanism for access token take care for expiry
	private static String accessToken;
	
	
	public String getAccessToken() {
		
		log.info("Getting access token from PayPal");
		
		  if(accessToken != null) {
			  log.info("Returning cached access token");
			  return accessToken;
		  }
		
		  log.info("No cached access token found, calling OAuth service ");
		// TODO call Paypal OAuth token retrieval logic
		
	String respone =	httpServiceEngine.makeHttpRequest();
	log.info("HTTP Response received: {}", respone);
	
		return "access token";
	}
	
}
