package com.paymentl.Constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

		
	GENERIC_ERROR("10000" , "Something went Wrong  please try again later  "),
	 CURRENT_CODE_REQUIRED("30001" , "Currency code is required field and cannot to null / blank "),
	 RETURN_URL_REQUIRED("30002" , "Return Url is required field and cannot be null / blank "),
	 INVALID_REQUEST("30003" , "Invalid request payload"),
	 INVALID_AMOUNT("30004" , "Amount must be greater than zero "), CANCEL_URL_REQUIRED("30005" , "Cancel Url is required field and cannot be null / blank ");
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode , String errorMessage){
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
}
