package com.att.developer.advice;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.fasterxml.jackson.core.JsonProcessingException;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ServerSideExceptionHandler {

	@ExceptionHandler(ServerSideException.class)
	@ResponseBody
	public ResponseEntity<ServerSideErrors> handleServerSideException(ServerSideException e) throws JsonProcessingException {
		
		ServerSideErrors errors = e.getServerSideErrors();
		
		if(errors == null) {
			errors = new ServerSideErrors();
		}
		
		return new ResponseEntity<ServerSideErrors>(errors, errors.getHttpStatus());
	}
}
