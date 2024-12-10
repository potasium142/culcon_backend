package com.culcon.backend.exceptions.custom;

import lombok.Getter;

import java.io.Serial;
import java.util.Map;

@Getter
public class RuntimeExceptionPlusPlus extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	Map<Object, Object> errorsTable;

	public RuntimeExceptionPlusPlus(String message, Map<Object, Object> errorsTable) {
		super(message);
		this.errorsTable = errorsTable;
	}
}
