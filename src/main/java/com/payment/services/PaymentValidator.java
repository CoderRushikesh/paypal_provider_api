package com.payment.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.payment.exception.PaypalProviderException;
import com.payment.pojo.CreateOrderReq;
import com.paymentl.Constant.ErrorCodeEnum;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PaymentValidator {

	public void validateCreateOrderRequest(CreateOrderReq request) {
	log.info("Validating Create Order Request: {}", request);
		
		
	   if(request == null) {
		   log.error("Create Order Request cannot be null ");
		   throw new PaypalProviderException(
				   ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),ErrorCodeEnum.INVALID_REQUEST.getErrorMessage(), HttpStatus.BAD_REQUEST);
	   }
	
		if (request.getReturnUrl() == null || request.getReturnUrl().isEmpty()) {
			log.error("Return URL is required field and cannot be null / blank ");
			throw new PaypalProviderException(
					ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorCode(),
					ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST
					);
		}

		
		if(request.getAmount() == null ||request.getAmount() <= 0) {
			log.error("Amount must be greater than zero ");
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
					ErrorCodeEnum.INVALID_AMOUNT.getErrorMessage(),
					HttpStatus.BAD_REQUEST
					);	
		}
		
		if (request.getCurrencyCode() == null || request.getCurrencyCode().isEmpty()) {
			log.error("Currency code is required field and cannot be null / blank ");
			throw new PaypalProviderException(
					ErrorCodeEnum.CURRENT_CODE_REQUIRED.getErrorCode(),
					ErrorCodeEnum.CURRENT_CODE_REQUIRED.getErrorMessage(),
					HttpStatus.BAD_REQUEST
					
					);
		}
		
		// check return url 
		if(request.getReturnUrl() == null && request.getReturnUrl().isEmpty()) {
			log.error("Return URL must start with http:// or https:// ");
			throw new PaypalProviderException(
					ErrorCodeEnum.INVALID_REQUEST.getErrorCode(),
					"Return URL must start with http:// or https://",
					HttpStatus.BAD_REQUEST
					);
		}
		
		// check cancel url if provided
		if(request.getCancelUrl() == null && request.getCancelUrl().isEmpty()) {
			if(!request.getCancelUrl().startsWith("http://") && !request.getCancelUrl().startsWith("https://")) {
				log.error("Cancel URL must start with http:// or https:// ");
				throw new PaypalProviderException(
						ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorCode(),
						ErrorCodeEnum.RETURN_URL_REQUIRED.getErrorMessage(),
						HttpStatus.BAD_REQUEST
						);
			}
		}
		
		// check cancle url
		if(request.getCancelUrl() == null || request.getCancelUrl().isEmpty()) {
			
				log.error("Cancel URL must start with http:// or https:// ");
				throw new PaypalProviderException(
						ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorCode(),
						ErrorCodeEnum.CANCEL_URL_REQUIRED.getErrorMessage(),
						HttpStatus.BAD_REQUEST
						);
			
		}
		
		log.info("Create Order Request validation passed.");
	}
	
	
}
