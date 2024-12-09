package com.culcon.backend.exceptions.custom;

import java.io.Serial;
import java.util.Map;

public class RuntimeExceptionPlusPlus extends RuntimeException {
	@Serial
	private static final long serialVersionUID = 1L;

	Map<Object, Object> errorsTable;

	public RuntimeExceptionPlusPlus(String message, Map<Object, Object> errorsTable) {
		super(message);
		this.errorsTable = errorsTable;
	}
}
