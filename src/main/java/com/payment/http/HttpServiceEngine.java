

package com.payment.http;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import com.payment.PaypalProviderServiceApplication;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class HttpServiceEngine {

    private final PaypalProviderServiceApplication paypalProviderServiceApplication;
    private final RestClient restClient;
 
    
    
    // Sandbox credentials from PayPal Developer Dashboard
    private static final String CLIENT_ID = "ARPevS9Y8_-qvuIRMkrnutgE0c4I4n9APy67mOqClpMYQeP5RJJMYQ2hsRXG1Q0JovdWn5vSZImP7MKC";
    private static final String CLIENT_SECRET = "EDUkTKHM5B5ExZALMmD-P3boqYfuHHhxf_Hlh4PQRcJCaotJR18YgizZsDVKE01fN3tWcPgpxuLYuwcG";
    
    
    public ResponseEntity<String> makeHttpRequest(HttpRequest httpRequest) {
        log.info("Making HTTP call to PayPal OAuth2 token endpoint...");

       
        // Execute request
        
        ResponseEntity<String> httpResponse = null;
		try {
			httpResponse = restClient
					.method(httpRequest.getHttpMethod())
			        .uri(httpRequest.getUrl())
			        .headers(h -> h.addAll(httpRequest.getHttpHeaders()))   // 
			        .body(httpRequest.getBody())
			        .retrieve()
			        .toEntity(String.class);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

//		
		
        log.info("Response from PayPal: {}" , httpResponse);
        return httpResponse;
    }

	
}



