package com.culcon.backend.exceptions.messages;

import lombok.Builder;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

@Builder
public record FieldErrorMessage(
	String fieldName,
	String message) {

	static public FieldErrorMessage objectErrorCast(ObjectError oe) {
		var fe = (FieldError) oe;
		return FieldErrorMessage.builder()
			.fieldName(fe.getField())
			.message(fe.getDefaultMessage())
			.build();
	}
}
