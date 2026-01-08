package com.payment.pojo;

import lombok.Data;

@Data
public class OrderResponse {
    private String id;
    private String status;
    private String redirectUrl;
    // Add more fields if needed (like payer info, amount, etc.)
}
