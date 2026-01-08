package com.payment.req;


import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class Amount {

    @JsonProperty("currency_code")
    private String currencyCode;

    private String value;

	public void setTotal(String string) {
		// TODO Auto-generated method stub
		
	}
}