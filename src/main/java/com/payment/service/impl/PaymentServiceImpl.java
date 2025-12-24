package com.payment.service.impl;

import org.springframework.stereotype.Service;

import com.payment.service.interfaces.PaymentService;
import com.payment.services.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private final TokenService tokenService;
	
	
	public String createOrder() {
		
		log.info("Creating order in PayPal");
		
		String accessToken = tokenService.getAccessToken();
		
		return "Order created with " + accessToken;
	}
	
}
