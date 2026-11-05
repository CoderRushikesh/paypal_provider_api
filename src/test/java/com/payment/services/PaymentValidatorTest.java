package com.payment.services;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import com.payment.exception.PaypalProviderException;
import com.payment.pojo.CreateOrderReq;
import com.paymentl.Constant.ErrorCodeEnum;

class PaymentValidatorTest {

	private final PaymentValidator validator = new PaymentValidator();

	private CreateOrderReq validRequest() {
		CreateOrderReq req = new CreateOrderReq();
		req.setAmount(100.50);
		req.setCurrencyCode("USD");
		req.setReturnUrl("https://example.com/success");
		req.setCancelUrl("https://example.com/cancel");
		return req;
	}

	private void assertRejected(CreateOrderReq req, ErrorCodeEnum expected) {
		PaypalProviderException ex = assertThrows(PaypalProviderException.class,
				() -> validator.validateCreateOrder(req));
		assertEquals(expected.getErrorCode(), ex.getErrorCode());
		assertEquals(HttpStatus.BAD_REQUEST, ex.getHttpStatus());
	}

	@Test
	void validRequestPasses() {
		assertDoesNotThrow(() -> validator.validateCreateOrder(validRequest()));
	}

	@Test
	void nullRequestIsRejected() {
		assertRejected(null, ErrorCodeEnum.INVALID_REQUEST);
	}

	@Test
	void zeroAmountIsRejected() {
		CreateOrderReq req = validRequest();
		req.setAmount(0.0);
		assertRejected(req, ErrorCodeEnum.INVALID_AMOUNT);
	}

	@Test
	void missingAmountIsRejected() {
		CreateOrderReq req = validRequest();
		req.setAmount(null);
		assertRejected(req, ErrorCodeEnum.INVALID_AMOUNT);
	}

	@Test
	void blankCurrencyIsRejected() {
		CreateOrderReq req = validRequest();
		req.setCurrencyCode(" ");
		assertRejected(req, ErrorCodeEnum.CURRENCY_CODE_REQUIRED);
	}

	@Test
	void missingReturnUrlIsRejected() {
		CreateOrderReq req = validRequest();
		req.setReturnUrl(null);
		assertRejected(req, ErrorCodeEnum.RETURN_URL_REQUIRED);
	}

	@Test
	void missingCancelUrlIsRejected() {
		CreateOrderReq req = validRequest();
		req.setCancelUrl("");
		assertRejected(req, ErrorCodeEnum.CANCEL_URL_REQUIRED);
	}
}
