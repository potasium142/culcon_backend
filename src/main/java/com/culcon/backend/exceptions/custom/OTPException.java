package com.culcon.backend.exceptions.custom;

import java.io.Serial;

public class OTPException extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	public OTPException(String message) {
		super(message);
	}
}
