package com.payment.services;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com.payment.exception.PaypalProviderException;
import com.payment.http.HttpRequest;
import com.payment.http.HttpServiceEngine;
import com.payment.res.PaypalOAuthToken;
import com.payment.util.JsonUtil;
import com.paymentl.Constant.Constant;
import com.paymentl.Constant.ErrorCodeEnum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Provides the PayPal OAuth2 access token.
 *
 * Flow:
 *   1. Look for the token in Redis  -> found: return it.
 *   2. Not in Redis (never stored, or its TTL expired) -> call the PayPal OAuth API.
 *   3. Store the new token in Redis with a TTL slightly shorter than PayPal's
 *      "expires_in", so Redis expires it for us (no manual expiry checks needed).
 *
 * If Redis is down, the service still works: it just calls PayPal each time.
 */

@Service
@Slf4j
@RequiredArgsConstructor
public class TokenService {

	private final HttpServiceEngine httpServiceEngine;
	private final JsonUtil jsonUtil;
	private final StringRedisTemplate redisTemplate;

	@Value("${paypal.client.id}")
	private String clientIdProp;

	@Value("${paypal.client.secret}")
	private String clientSecretProp;

	@Value("${paypal.oauth.url}")
	private String oauthUrl;

	@Value("${paypal.token.redis-key:paypal:provider:oauth:access_token}")
	private String redisKey;

	@Value("${paypal.token.expiry-buffer-seconds:300}")
	private long expiryBufferSeconds;

	/** Guards the "check Redis -> call PayPal -> store" sequence inside this JVM. */
	private final Object refreshLock = new Object();

	public String getAccessToken() {
		log.info("Getting PayPal access token");

		// 1. Fast path: token already in Redis
		String cached = readFromRedis();
		if (cached != null) {
			log.info("Returning access token from Redis");
			return cached;
		}

		// 2. Not in Redis -> fetch from PayPal (one thread at a time)
		synchronized (refreshLock) {
			// another thread may have refreshed it while we waited for the lock
			cached = readFromRedis();
			if (cached != null) {
				log.info("Returning access token from Redis (refreshed by another thread)");
				return cached;
			}

			log.info("No access token in Redis, calling PayPal OAuth API");
			PaypalOAuthToken token = requestNewToken();

			// 3. Store in Redis with a TTL
			writeToRedis(token);
			return token.getAccessToken();
		}
	}

	/** Removes the cached token, e.g. call this when PayPal answers 401 for a cached token. */
	public void clearToken() {
		try {
			redisTemplate.delete(redisKey);
			log.info("Access token removed from Redis");
		} catch (DataAccessException e) {
			log.warn("Could not remove access token from Redis: {}", e.getMessage());
		}
	}

	// ---------------------------------------------------------------- Redis

	private String readFromRedis() {
		try {
			String token = redisTemplate.opsForValue().get(redisKey);
			return (token == null || token.isBlank()) ? null : token;
		} catch (DataAccessException e) {
			// Redis problem must not break payments: treat as "cache miss"
			log.warn("Redis unavailable while reading access token, falling back to PayPal: {}",
					e.getMessage());
			return null;
		}
	}

	private void writeToRedis(PaypalOAuthToken token) {
		long ttlSeconds = calculateTtlSeconds(token.getExpiresIn());
		try {
			redisTemplate.opsForValue().set(redisKey, token.getAccessToken(), Duration.ofSeconds(ttlSeconds));
			log.info("Access token stored in Redis, TTL = {} seconds", ttlSeconds);
		} catch (DataAccessException e) {
			log.warn("Redis unavailable while storing access token, continuing without cache: {}",
					e.getMessage());
		}
	}

	/** expires_in minus a safety buffer; never zero/negative (Redis rejects a 0 TTL). */
	long calculateTtlSeconds(long expiresInSeconds) {
		if (expiresInSeconds > expiryBufferSeconds) {
			return expiresInSeconds - expiryBufferSeconds;
		}
		// very short-lived token: use half of its life, at least 1 second
		return Math.max(expiresInSeconds / 2, 1);
	}

	// --------------------------------------------------------------- PayPal

	private PaypalOAuthToken requestNewToken() {
		String clientId = clientIdProp == null ? "" : clientIdProp.trim();
		String clientSecret = clientSecretProp == null ? "" : clientSecretProp.trim();
		if (clientId.isEmpty() || clientSecret.isEmpty()) {
			log.error("PayPal credentials missing: set PAYPAL_CLIENT_ID and PAYPAL_CLIENT_SECRET");
			throw new PaypalProviderException(
					ErrorCodeEnum.PAYPAL_API_UNAUTHORIZED.getErrorCode(),
					ErrorCodeEnum.PAYPAL_API_UNAUTHORIZED.getErrorMessage(),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(clientId, clientSecret); // generates the Authorization header
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add(Constant.GRANT_TYPE, Constant.CLIENT_CREDENTIALS);

		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setHttpMethod(HttpMethod.POST);
		httpRequest.setUrl(oauthUrl);
		httpRequest.setHttpHeaders(headers);
		httpRequest.setBody(formData);

		// do not log the request: it contains the Basic auth header (client secret)
		ResponseEntity<String> response = httpServiceEngine.makeHttpRequest(httpRequest);
		log.info("PayPal OAuth response status: {}", response.getStatusCode());

		// HttpServiceEngine returns 4xx responses instead of throwing, so check it here
		if (!response.getStatusCode().is2xxSuccessful()) {
			log.error("PayPal OAuth call failed, status: {}", response.getStatusCode());
			throw new PaypalProviderException(
					ErrorCodeEnum.PAYPAL_API_UNAUTHORIZED.getErrorCode(),
					ErrorCodeEnum.PAYPAL_API_UNAUTHORIZED.getErrorMessage(),
					HttpStatus.valueOf(response.getStatusCode().value()));
		}

		PaypalOAuthToken token = jsonUtil.fromJson(response.getBody(), PaypalOAuthToken.class);
		if (token == null || token.getAccessToken() == null || token.getAccessToken().isBlank()) {
			log.error("PayPal OAuth response did not contain an access_token");
			throw new PaypalProviderException(
					ErrorCodeEnum.PAYPAL_API_ERROR.getErrorCode(),
					ErrorCodeEnum.PAYPAL_API_ERROR.getErrorMessage(),
					HttpStatus.BAD_GATEWAY);
		}
		return token;
	}
}