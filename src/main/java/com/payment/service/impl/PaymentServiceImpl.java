package com.payment.service.impl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import com.payment.exception.PaypalProviderException;
import com.payment.http.HttpRequest;
import com.payment.http.HttpServiceEngine;
import com.payment.pojo.CreateOrderReq;
import com.payment.pojo.OrderResponse;
import com.payment.res.PaypalOrder;
import com.payment.service.interfaces.PaymentService;
import com.payment.services.TokenService;
import com.payment.services.helper.createOrderHelpter;
import com.payment.util.JsonUtil;
import com.paymentl.Constant.ErrorCodeEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

   
	private final TokenService tokenService;
    private final HttpServiceEngine httpServiceEngine;
    private final JsonUtil jsonUtil;
    private final createOrderHelpter CreateOrderHelpter;	

    @Override
    public OrderResponse createOrder(CreateOrderReq createOrderReq) {
        log.info("Creating order in PayPal");
        

        if(createOrderReq.getReturnUrl() == null) {
        	log.error("Return URL is required field and cannot be null / black ");
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
					ErrorCodeEnum.INVALID_REQUEST.getErrorMessage(),
					HttpStatus.BAD_REQUEST
					);
		}
        
        // currentCode is not pass - 4xx 400 bad request 
        if(createOrderReq.getCurrencyCode() == null || createOrderReq.getCurrencyCode().isEmpty()) {
        	
        	throw new PaypalProviderException(
        			ErrorCodeEnum.CURRENT_CODE_REQUIRED.getErrorCode(),
        			ErrorCodeEnum.CURRENT_CODE_REQUIRED.getErrorMessage(),
        			HttpStatus.BAD_REQUEST
        			);
        }
        
        String accessToken = tokenService.getAccessToken();
        log.info("Access Token retrieved: {}", accessToken);

        HttpRequest httpRequest = CreateOrderHelpter.prepareCreateOrderHttpRequest(createOrderReq, accessToken);
        log.info("Prepared HTTP Request: {}", httpRequest);

        ResponseEntity<String> successResponse = httpServiceEngine.makeHttpRequest(httpRequest);
        if (successResponse == null || successResponse.getBody() == null) {
            log.error("PayPal API returned null response");
            throw new RuntimeException("PayPal API returned null response");
        }
        log.info("HTTP Response received: {}", successResponse);

  
        PaypalOrder paypalOrder =  jsonUtil.fromJson(successResponse.getBody(), PaypalOrder.class);
        log.info("Parsed PaypalOrder object: {}", paypalOrder);

        OrderResponse orderResponse = CreateOrderHelpter.toOrderResponse(paypalOrder);
        log.info("Mapped OrderResponse object: {}", orderResponse);

        return orderResponse; // return useful info instead of hardcoded string
    }



	
  
}
