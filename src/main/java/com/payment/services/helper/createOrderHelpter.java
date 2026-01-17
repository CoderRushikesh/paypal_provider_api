package com.payment.services.helper;

import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.payment.http.HttpRequest;
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
import com.payment.util.JsonUtil;
import com.paymentl.Constant.Constant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class createOrderHelpter {

	 private final JsonUtil jsonUtil;

	    @Value("${paypal.createOrder.url}")
	    private String createOrderUrl;
	    
	public HttpRequest prepareCreateOrderHttpRequest(CreateOrderReq createOrderReq, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String uuid = UUID.randomUUID().toString();
        log.info("Generated UUID for PayPal request: {}", uuid);
        headers.add(Constant.PAY_PAL_REQUEST_ID, uuid);

        Amount amount = new Amount();
        amount.setCurrencyCode(createOrderReq.getCurrencyCode());
        amount.setValue(String.format(Constant._2F, createOrderReq.getAmount()));

        PurchaseUnit unit = new PurchaseUnit();
        unit.setAmount(amount);

        ExperienceContext ctx = new ExperienceContext();
        ctx.setPaymentMethodPreference(Constant.IMMEDIATE_PAYMENT_REQUIRED);
        ctx.setLandingPage(Constant.LANDING_PAGE_LOGIN);
        ctx.setShippingPreference(Constant.NO_SHIPPING);
        ctx.setUserAction(Constant.PAY_NOW);
        ctx.setReturnUrl(createOrderReq.getReturnUrl());
        ctx.setCancelUrl(createOrderReq.getCancelUrl());

        Paypal paypal = new Paypal();
        paypal.setExperienceContext(ctx);
        PaymentSource ps = new PaymentSource();
        ps.setPaypal(paypal);
        OrderRequest order = new OrderRequest();
        order.setIntent(Constant.CAPTURE);
        order.setPurchaseUnits(Collections.singletonList(unit));
        order.setPaymentSource(ps);

        
   // convert to json String 
        String requestAsJson=  jsonUtil.toJson(order);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setHttpMethod(HttpMethod.POST);
        httpRequest.setUrl(createOrderUrl);
        httpRequest.setHttpHeaders(headers);
        httpRequest.setBody(requestAsJson);
		return httpRequest;
	}
    
	  public OrderResponse toOrderResponse(PaypalOrder paypalOrder) {
	        log.info("Mapping PaypalOrder to OrderResponse: {}", paypalOrder);

	        OrderResponse response = new OrderResponse();
	        response.setId(paypalOrder.getId());
	        response.setStatus(paypalOrder.getStatus());

//	        String redirectLink = paypalOrder.getLinks().stream()
//	                .filter(link -> "approve".equals(link.getRel()))
//	                .findFirst()
//	                .map(PaypalLink::getHref)
//	                .orElse(null);
	        
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
