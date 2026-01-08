package com.controller;


import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.pojo.CreateOrderReq;
import com.payment.service.interfaces.PaymentService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
@RequiredArgsConstructor
public class paymentController {
	
	private final PaymentService paymentService;
	
	
	@PostMapping("/Payments")
	public String createOrder(@RequestBody CreateOrderReq createOrderReq) {
	
		log.info("Creating Order in paypal provider service +||createOrderReq: {}", createOrderReq);
		
		
		String response = paymentService.createOrder(createOrderReq);
		
		log.info("Response from payment service: {}", response);
		return  response;
	}
    
	

	@PostConstruct
	public void init() {
		LoggerFactory.getLogger(paymentController.class).info("paymentController initialized");
	}
    
}
