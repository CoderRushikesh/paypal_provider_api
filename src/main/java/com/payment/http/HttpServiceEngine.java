package com.payment.http;

import java.net.http.HttpHeaders;
import java.util.function.Consumer;

import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

	private final RestClient restClient;
	
	
	public String makeHttpRequest() {
		// Simulate making an HTTP request
		log.info("Makiing Http call in HttpServiceEngine");
		
		HttpHeaders headers = new HttpHeaders();
		String ClientId = "Abcd1234";
		String ClientSecret = "XyZ9876";
		headers.setBasicAuth( ClientId, ClientSecret);
		
//		HttpHeaders headers = HttpHeaders.of( (k,v) -> true);
		
		

		
		class ConsumerHeaderObject implements Consumer<HttpHeaders>{

			@Override
			public void accept(HttpHeaders t) {
				// TODO Auto-generated method stub
				
			}
/
		}
		
		
		
		restClient.method(HttpMethod.POST)
		        .uri("https://api-m.sandbox.paypal.com/v1/oauth2/token\r\n")
		        .header("Content-Type", "application/x-www-form-urlencoded")
		        
		
//		RestClient restClient = RestClient.create();
		return "http_Response";
	}
	
	
}
