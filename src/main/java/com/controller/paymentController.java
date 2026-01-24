package com.controller;


import org.slf4j.LoggerFactory;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.pojo.CreateOrderReq;
import com.payment.pojo.OrderResponse;
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
	public OrderResponse createOrder(@RequestBody CreateOrderReq createOrderReq) {
	
		log.info("Creating Order in paypal provider service +||createOrderReq: {}", createOrderReq);
		
		
		OrderResponse response = paymentService.createOrder(createOrderReq);
		
		log.info("Response from payment service: {}", response);
		return  response;
	}
    
	@PostMapping("/{orderId}/capture")
	public OrderResponse captureOrder(@PathVariable String orderId) {
		log.info("Capturing order in PayPal provider service"
				+ "||orderId:{}",
				orderId);
		
		OrderResponse response = paymentService.captureOrder(orderId);
		log.info("Order capture response from service: {}", response);
		
		return response;
	}
	

	@PostConstruct
	public void init() {
		LoggerFactory.getLogger(paymentController.class).info("paymentController initialized");
	}
    
}
