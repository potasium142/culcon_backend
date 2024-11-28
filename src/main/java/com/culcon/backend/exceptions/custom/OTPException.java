package com.culcon.backend.exceptions.custom;

public class OTPException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public OTPException(String message) {
		super(message);
	}
}
