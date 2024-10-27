package com.culcon.backend.exceptions;

import java.util.HashMap;
import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.culcon.backend.exceptions.messages.ExceptionMessage;
import com.culcon.backend.exceptions.messages.FieldErrorMessage;

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

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> dataIntegerityViolation(DataIntegrityViolationException ex) {
        return new ResponseEntity<>(
                ExceptionMessage.map(ex),
                HttpStatus.NOT_ACCEPTABLE);
    }

    @ExceptionHandler(TransactionSystemException.class)
    public ResponseEntity<?> transactionSystem(TransactionSystemException ex) {
        return new ResponseEntity<>(
                ex.getCause(),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> noSuchElement(NoSuchElementException ex) {
        return new ResponseEntity<>(
                ExceptionMessage.map(ex),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> genericException(Exception ex) {
        return new ResponseEntity<>(
                ExceptionMessage.map(ex),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
