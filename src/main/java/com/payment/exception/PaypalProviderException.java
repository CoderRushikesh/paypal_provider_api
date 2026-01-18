package com.payment.exception;

import lombok.Getter;

@Getter
public class PaypalProviderException  extends RuntimeException{

	private static final long serialVersionUID = 1L;
	private final String errorCode;
	 private final String errorMessage;

	public PaypalProviderException(String errorCode, String errorMessage) {
		super(errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	
	
}
