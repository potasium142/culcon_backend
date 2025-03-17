package com.culcon.backend.exceptions;

import com.culcon.backend.exceptions.custom.OTPException;
import com.culcon.backend.exceptions.custom.RuntimeExceptionPlusPlus;
import com.culcon.backend.exceptions.messages.ExceptionMessage;
import com.culcon.backend.exceptions.messages.FieldErrorMessage;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

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
	public ResponseEntity<?> dataIntegrityViolation(DataIntegrityViolationException ex) {
		var violation = (org.hibernate.exception.ConstraintViolationException) ex.getCause();
		var error = FieldErrorMessage.builder()
			.fieldName("Data integrity violation")
			.message(violation.getSQLException().getLocalizedMessage())
			.build();
		return new ResponseEntity<>(
			error,
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

	@ExceptionHandler(UsernameNotFoundException.class)
	public ResponseEntity<?> usernameNotFound(UsernameNotFoundException ex) {
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(RuntimeExceptionPlusPlus.class)
	public ResponseEntity<?> runtimeExceptionPlusPlus(RuntimeExceptionPlusPlus ex) {
		return new ResponseEntity<>(
			Map.of(
				"cause", RuntimeExceptionPlusPlus.class.getSimpleName(),
				"message", ex.getLocalizedMessage(),
				"errors", ex.getErrorsTable()
			), HttpStatus.INTERNAL_SERVER_ERROR
		);
	}

	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<?> expiredJwtException(ExpiredJwtException ex) {
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.UNAUTHORIZED
		);
	}

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<?> constraintViolationException(ConstraintViolationException ex) {
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.NOT_ACCEPTABLE
		);
	}

	@ExceptionHandler(OTPException.class)
	public ResponseEntity<?> otpException(OTPException ex) {
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.NOT_ACCEPTABLE
		);
	}

	@ExceptionHandler(LockedException.class)
	public ResponseEntity<?> lockedException(LockedException ex) {
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.LOCKED
		);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> genericException(Exception ex) {
		ex.printStackTrace();
		return new ResponseEntity<>(
			ExceptionMessage.map(ex),
			HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
