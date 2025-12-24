package com.controller;


import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.service.interfaces.PaymentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class paymentController {
	
	private final PaymentService paymentServiceImpl;
	
	
	@PostMapping("/payments")
	public String createOrder() {
	
		String response = PaymentService.createOrder();
		
		log.info("Response from payment service: {}", response);
		return "Order created with  " + response;
	}
    
    
}
