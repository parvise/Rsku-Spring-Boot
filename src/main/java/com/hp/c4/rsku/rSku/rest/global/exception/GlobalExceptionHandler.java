package com.hp.c4.rsku.rSku.rest.global.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

	@Value("${rsku.error.key}")
	public String C4_EXCEPTION_KEY;

	@Value("${rsku.error.value}")
	public String C4_EXCEPTION_VALUE;

	@ExceptionHandler(Exception.class)
	public final ResponseEntity<Map<String, String>> handleAllExceptions(Exception ex, WebRequest request) {
		Map<String, String> details = new HashMap<>();
		details.put(C4_EXCEPTION_KEY, C4_EXCEPTION_VALUE);
		return new ResponseEntity<>(details, HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
