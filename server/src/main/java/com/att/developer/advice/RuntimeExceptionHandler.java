package com.att.developer.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class RuntimeExceptionHandler {

	@ExceptionHandler(RuntimeException.class)
	@ResponseBody
	public ResponseEntity<String> handleRuntimeException(RuntimeException e) throws JsonProcessingException {
		ServerSideErrors errors = new ServerSideErrors();
		String errorMessage = (e.getCause() == null) ? e.getMessage() : e.getCause().getMessage();
		ServerSideError error = new ServerSideError.Builder().id("Unexpected").message(errorMessage).build();
		errors.add(error);
		
		ObjectMapper jsonMapper = new ObjectMapper();
		
		return new ResponseEntity<String>(jsonMapper.writeValueAsString(errors), HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
