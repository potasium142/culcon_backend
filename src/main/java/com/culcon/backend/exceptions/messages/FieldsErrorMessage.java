package com.culcon.backend.exceptions.messages;

import lombok.Builder;

@Builder
public record FieldsErrorMessage(
	String fieldName,
	String[] message) {

	static public FieldsErrorMessage objectErrorCast(
		String fieldName,
		String... messages
	) {
		return FieldsErrorMessage.builder()
			.fieldName(fieldName)
			.message(messages)
			.build();
	}
}
