package com.culcon.backend.exceptions.messages;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import lombok.Builder;

@Builder
public record FieldErrorMessage(
        String fieldName,
        String message) {

    static public FieldErrorMessage objectErrorCast(ObjectError oe) {
        var fe = FieldError.class.cast(oe);
        return FieldErrorMessage.builder()
                .fieldName(fe.getField())
                .message(fe.getDefaultMessage())
                .build();
    }
}
