package com.payment.service.impl;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.payment.http.HttpRequest;
import com.payment.http.HttpServiceEngine;
import com.payment.pojo.CreateOrderReq;
import com.payment.pojo.OrderResponse;
import com.payment.req.Amount;
import com.payment.req.ExperienceContext;
import com.payment.req.OrderRequest;
import com.payment.req.PaymentSource;
import com.payment.req.Paypal;
import com.payment.req.PurchaseUnit;
import com.payment.res.PaypalOrder;
import com.payment.service.interfaces.PaymentService;
import com.payment.services.TokenService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

	private static final String CAPTURE = "CAPTURE";
	private static final String PAY_NOW = "PAY_NOW";
	private static final String NO_SHIPPING = "NO_SHIPPING";
	private static final String LANDING_PAGE_LOGIN = "LOGIN";
	private static final String IMMEDIATE_PAYMENT_REQUIRED = "IMMEDIATE_PAYMENT_REQUIRED";
	private static final String _2F = "%.2f";
	private static final String PAY_PAL_REQUEST_ID = "PayPal-Request-Id";
	private final TokenService tokenService;
	private final ObjectMapper mapper;
    private final HttpServiceEngine httpServiceEngine;
   @Value("paypal.createOrder.url")
    private String CreateOrder_url ; 

    public String createOrder(CreateOrderReq createOrderReq) {

		log.info("Creating order in PayPal");

		String accessToken = tokenService.getAccessToken();
		log.info(" Access Token retrived : {}",accessToken);



		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(accessToken);
		headers.setContentType(MediaType.APPLICATION_JSON);


		// set headers Paypal , request id => UUID
		String uuid = UUID.randomUUID().toString();
		log.info("Generated UUID for PayPal request: {}", uuid);
		headers.add(PAY_PAL_REQUEST_ID, uuid);


		
		Amount amount = new Amount();
		
		amount.setCurrencyCode(createOrderReq.getCurrencyCode());
		
		// read the amount from createOrderReq and convert to 2 decimal places
		String amtStr = String.format(_2F, createOrderReq.getAmount());
		
		amount.setValue(amtStr);

		// Create purchese unit
		PurchaseUnit unit = new PurchaseUnit();
		unit.setAmount(amount);

		// Exprience context
		ExperienceContext ctx = new ExperienceContext();
		ctx.setPaymentMethodPreference(IMMEDIATE_PAYMENT_REQUIRED);
		ctx.setLandingPage(LANDING_PAGE_LOGIN);
		ctx.setShippingPreference(NO_SHIPPING);
		ctx.setUserAction(PAY_NOW);
		ctx.setReturnUrl(createOrderReq.getReturnUrl());
		ctx.setCancelUrl(createOrderReq.getCancelUrl());


		// Paypal object
		Paypal paypal = new Paypal();
		paypal.setExperienceContext(ctx);

		// payment source
		PaymentSource ps = new PaymentSource();
		ps.setPaypal(paypal);

		// final order request
		OrderRequest order = new OrderRequest();
		order.setIntent(CAPTURE);
		order.setPurchaseUnits(Collections.singletonList(unit));
		order.setPaymentSource(ps);

		
		log.info("Constructed Order Object: {}", order);
		// Convert order object to JSON
		
		String requestAsJson = null;
		try {
			requestAsJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(order);
			log.info("Order JSON: {}", requestAsJson);

		} catch (Exception e) {
			log.error("Error converting order to JSON", e);
			throw new RuntimeException("Error converting order to JSON", e);
		}




		// Prepare form data
		//		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		//		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS );


		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		
		httpRequest.setUrl(CreateOrder_url);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(requestAsJson);


		log.info("Prepared HTTP Request for OAuth token: {}", httpRequest);

    ResponseEntity<String> successResponse =   httpServiceEngine.makeHttpRequest(httpRequest);
      log.info("HTTP Response received: {}", successResponse);

      //use model mapper to convert response body to PaypalOrder object
      PaypalOrder paypalOrder = null;
      try {
         paypalOrder =	mapper.readValue(successResponse.getBody(), PaypalOrder.class);
	} catch (Exception e) {
	   log.error("Error parsing PayPal order response", e);
	   throw new RuntimeException("Failed to parse PayPal order response", e);
	}
      
		return successResponse.getBody(); 
	}
	
	
    public OrderResponse toOrderResponse(PaypalOrder paypalOrder) {
    	
    	log.info("Mapping PaypalOrder to OrderResponse: {}", paypalOrder);
    	
		OrderResponse response = new OrderResponse();
		response.setId(paypalOrder.getId());
		response.setStatus(paypalOrder.getStatus());
		// Add more fields as necessary
		
		String redirectLink = paypalOrder.getLinks().stream()
				.filter(link -> "approve".equals(link.getRel()))
				.findFirst()
				.map(link -> link.getHref())
			    .orElse(null);
		
		response.setRedirectUrl(redirectLink);
		
		log.info("Mapped OrderResponse: {}", response);
		
		return response;
	}
	

}
