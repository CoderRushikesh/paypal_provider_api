package com.payment.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.payment.pojo.ErrorResponse;

import lombok.extern.slf4j.Slf4j;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
			
   
	
    @ExceptionHandler(PaypalProviderException.class)
	public ResponseEntity<ErrorResponse> handlePaypalProviderException(PaypalProviderException ex) {
	log.error("PaypalProviderException: ", ex);
    	ErrorResponse error = new ErrorResponse(ex.getErrorCode() , ex.getMessage());	
		return new ResponseEntity<>(error,  HttpStatus.BAD_REQUEST);
	}
	
}
