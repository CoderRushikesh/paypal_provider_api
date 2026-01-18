package com.paymentl.Constant;

import lombok.Getter;

@Getter
public enum ErrorCodeEnum {

		
	 CURRENT_CODE_REQUIRED("30001" , "Currency code is required field and cannot to null / blank "); 
	 
	
	
	private final String errorCode;
	private final String errorMessage;
	
	ErrorCodeEnum(String errorCode , String errorMessage){
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
}
