package com.payment.pojo;

import lombok.Data;

@Data
public class CreateOrderReq {

	public String currencyCode;
	private Double amount;
	private String returnUrl;
	private String cancelUrl;
	
	
	
}
