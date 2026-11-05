package com.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.payment.http.HttpRequest;
import com.payment.http.HttpServiceEngine;
import com.payment.res.PaypalOAuthToken;
import com.payment.util.JsonUtil;
import com.paymentl.Constant.Constant;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

	private static final long REFRESH_BEFORE_EXPIRY_SECONDS = 300;

	private final HttpServiceEngine httpServiceEngine;
	private final JsonUtil jsonUtil;

	@Value("${paypal.client.id}")
	private String clientIdProp;

	@Value("${paypal.client.secret}")
	private String clientSecretProp;

	@Value("${paypal.oauth.url}")
	private String oauthUrl;

	private String accessToken;
	private Instant expiresAt = Instant.EPOCH;

	public synchronized String getAccessToken() {
		if (accessToken != null && Instant.now().isBefore(expiresAt)) {
			return accessToken;
		}

		String clientId = clientIdProp == null ? "" : clientIdProp.trim();
		String clientSecret = clientSecretProp == null ? "" : clientSecretProp.trim();
		if (clientId.isEmpty() || clientSecret.isEmpty()) {
			throw new IllegalStateException(
					"PayPal credentials missing: set PAYPAL_CLIENT_ID and PAYPAL_CLIENT_SECRET");
		}

		log.info("Requesting new PayPal access token from {}", oauthUrl);

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId, clientSecret);
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(oauthUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(formData);

		ResponseEntity<String> response = httpServiceEngine.makeHttpRequest(httpRequest);
		log.info("OAuth response status: {}", response.getStatusCode());

		PaypalOAuthToken token = jsonUtil.fromJson(response.getBody(), PaypalOAuthToken.class);
		accessToken = token.getAccessToken();
		long lifetime = Math.max(token.getExpiresIn() - REFRESH_BEFORE_EXPIRY_SECONDS, 0);
		expiresAt = Instant.now().plusSeconds(lifetime);
		log.info("Cached new access token, refreshes in {} seconds", lifetime);
		return accessToken;
	}

	public synchronized void clearToken() {
		accessToken = null;
		expiresAt = Instant.EPOCH;
	}
}