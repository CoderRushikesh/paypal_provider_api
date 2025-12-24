package com.payment.services;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TokenService {

	public String getAccessToken() {
		
		log.info("Getting access token from PayPal");
		
		return "access token";
	}
	
}
