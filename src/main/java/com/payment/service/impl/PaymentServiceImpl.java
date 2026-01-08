package com.payment.service.impl;

import java.util.Collections;
import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.http.HttpRequest;
import com.payment.req.Amount;
import com.payment.req.ExperienceContext;
import com.payment.req.OrderRequest;
import com.payment.req.PaymentSource;
import com.payment.req.Paypal;
import com.payment.req.PurchaseUnit;
import com.payment.service.interfaces.PaymentService;
import com.payment.services.TokenService;
import com.paymentl.Constant.Constant;

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
		log.info(" Access Token retrived : {}",accessToken);
		
		
		
		HttpHeaders headers = new HttpHeaders();
	   headers.setBearerAuth(accessToken);
	   headers.setContentType(MediaType.APPLICATION_JSON);
	   
	   
	   // set headers Paypal , request id => UUID
	   String uuid = UUID.randomUUID().toString();
	    log.info("Generated UUID for PayPal request: {}", uuid);
	    headers.add("Paypal-Request-Id", uuid);
	   
	    
	    Amount amount = new Amount();
	    amount.setCurrencyCode("USD");
	    amount.setTotal("10.00");
	    
	    // Create purchese unit
	    PurchaseUnit unit = new PurchaseUnit();
	    unit.setAmount(amount);
	    
	    // Exprience context
	    ExperienceContext ctx = new ExperienceContext();
		ctx.setPaymentMethodPreference("IMMEDIATE_PAYMENT_REQUIRED");
	    ctx.setLandingPage("LOGIN");
	    ctx.setUserAction("PAY_NOW");
	    ctx.setReturnUrl("https://example.com/return");
	    ctx.setCancelUrl("https://example.com/cancel");
	    
	    
	    // Paypal object
	    Paypal paypal = new Paypal();
	    paypal.setExperienceContext(ctx);
	    
	    // payment source
	    PaymentSource ps = new PaymentSource();
	    ps.setPaypal(paypal);
	    
	    // final order request
	    OrderRequest order = new OrderRequest();
		order.setIntent("CAPTURE");
		order.setPurchaseUnits(Collections.singletonList(unit));
	    order.setPaymentSource(ps);
	    
	    // Convert order object to JSON
	    ObjectMapper mapper = new ObjectMapper();
	    
	    try {
			String json = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(order);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	    // Prepare form data
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS );


		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
//		httpRequest.setUrl(outhUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(formData);


		log.info("Prepared HTTP Request for OAuth token: {}", httpRequest);


		
		
		
		
		return "Order created with " + accessToken;
	}
	
}
