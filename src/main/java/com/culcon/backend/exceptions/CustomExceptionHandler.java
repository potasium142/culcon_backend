package com.culcon.backend.exceptions;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.culcon.backend.exceptions.messages.FieldErrorMessage;

import java.util.HashMap;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            @NonNull MethodArgumentNotValidException ex,
            @NonNull HttpHeaders headers,
            @NonNull HttpStatusCode status,
            @NonNull WebRequest request) {

        var body = new HashMap<String, Object>();

        body.put("exception", ex.getClass().getSimpleName());

        var errors = ex.getBindingResult()
                .getAllErrors()
                .stream()
                .map(FieldErrorMessage::objectErrorCast)
                .toArray();
        body.put("errors", errors);

        return new ResponseEntity<>(body, headers, status);
    }

}
