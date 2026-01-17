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
import com.payment.res.PaypalLink;
import com.payment.res.PaypalOrder;
import com.payment.service.interfaces.PaymentService;
import com.payment.services.TokenService;
import com.payment.util.JsonUtil;

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
    private final JsonUtil jsonUtil;
    
    
    @Value("${paypal.createOrder.url}")
    private String createOrderUrl;

    @Override
    public OrderResponse createOrder(CreateOrderReq createOrderReq) {
        log.info("Creating order in PayPal");

        String accessToken = tokenService.getAccessToken();
        log.info("Access Token retrieved: {}", accessToken);

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uuid = UUID.randomUUID().toString();
        log.info("Generated UUID for PayPal request: {}", uuid);
        headers.add(PAY_PAL_REQUEST_ID, uuid);

        Amount amount = new Amount();
        amount.setCurrencyCode(createOrderReq.getCurrencyCode());
        amount.setValue(String.format(_2F, createOrderReq.getAmount()));

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        ExperienceContext ctx = new ExperienceContext();
        ctx.setPaymentMethodPreference(IMMEDIATE_PAYMENT_REQUIRED);
        ctx.setLandingPage(LANDING_PAGE_LOGIN);
        ctx.setShippingPreference(NO_SHIPPING);
        ctx.setUserAction(PAY_NOW);
        ctx.setReturnUrl(createOrderReq.getReturnUrl());
        ctx.setCancelUrl(createOrderReq.getCancelUrl());

        Paypal paypal = new Paypal();
        paypal.setExperienceContext(ctx);

        PaymentSource ps = new PaymentSource();
        ps.setPaypal(paypal);

        OrderRequest order = new OrderRequest();
        order.setIntent(CAPTURE);
        order.setPurchaseUnits(Collections.singletonList(unit));
        order.setPaymentSource(ps);

        
   // convert to json String 
        String requestAsJson=  jsonUtil.toJson(order);
        
        
//        String requestAsJson;
//        try {
//            requestAsJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(order);
//            log.info("Order JSON: {}", requestAsJson);
//        } catch (Exception e) {
//            log.error("Error converting order to JSON", e);
//            throw new RuntimeException("Error converting order to JSON", e);
//        }

        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(createOrderUrl);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setBody(requestAsJson);

        log.info("Prepared HTTP Request: {}", httpRequest);

        ResponseEntity<String> successResponse = httpServiceEngine.makeHttpRequest(httpRequest);

        if (successResponse == null || successResponse.getBody() == null) {
            log.error("PayPal API returned null response");
            throw new RuntimeException("PayPal API returned null response");
        }

        log.info("HTTP Response received: {}", successResponse);

  
 // 
        PaypalOrder paypalOrder=   jsonUtil.fromJson(successResponse.getBody(), PaypalOrder.class);
             
//        PaypalOrder paypalOrder;
//        try {
//            paypalOrder = mapper.readValue(successResponse.getBody(), PaypalOrder.class);
//        } catch (Exception e) {
//            log.error("Error parsing PayPal order response", e);
//            throw new RuntimeException("Failed to parse PayPal order response", e);
//        }

        log.info("Parsed PaypalOrder object: {}", paypalOrder);

        OrderResponse orderResponse = toOrderResponse(paypalOrder);
        log.info("Mapped OrderResponse object: {}", orderResponse);

        return orderResponse; // return useful info instead of hardcoded string
    }
    
    

    private OrderResponse toOrderResponse(PaypalOrder paypalOrder) {
        log.info("Mapping PaypalOrder to OrderResponse: {}", paypalOrder);

        OrderResponse response = new OrderResponse();
        response.setId(paypalOrder.getId());
        response.setStatus(paypalOrder.getStatus());

//        String redirectLink = paypalOrder.getLinks().stream()
//                .filter(link -> "approve".equals(link.getRel()))
//                .findFirst()
//                .map(PaypalLink::getHref)
//                .orElse(null);
        
        String redirectLink = paypalOrder.getLinks().stream()
                .filter(link ->
                        "approve".equalsIgnoreCase(link.getRel()) ||
                        "payer-action".equalsIgnoreCase(link.getRel()) ||
                        "checkout".equalsIgnoreCase(link.getRel())
                )
                .findFirst()
                .map(PaypalLink::getHref)
                .orElse(null);


        response.setRedirectUrl(redirectLink);

        log.info("Mapped OrderResponse: {}", response);

        return response;
    }
}
