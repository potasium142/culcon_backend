package com.culcon.backend.exceptions.messages;

import lombok.Builder;

@Builder
public record ExceptionMessage(
        String cause,
        String messages) {

    public static ExceptionMessage map(Exception ex) {
        return ExceptionMessage.builder()
                .cause(ex.getClass().getSimpleName())
                .messages(ex.getMessage()).build();
    }
}
