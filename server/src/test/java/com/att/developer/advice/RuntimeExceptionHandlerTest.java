package com.att.developer.advice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.core.JsonProcessingException;

public class RuntimeExceptionHandlerTest {

	RuntimeExceptionHandler runtimeExceptionHandler = null;
	
	@Before
	public void init() {
		runtimeExceptionHandler = new RuntimeExceptionHandler();
	}
	
	@Test
	public void testHandleRuntimeException() throws JsonProcessingException {
		RuntimeException e = new RuntimeException("Something went wrong");
		ResponseEntity<String> errorResponse = runtimeExceptionHandler.handleRuntimeException(e);
		
		Assert.assertEquals("{\"errors\":[{\"id\":\"Unexpected\",\"message\":\"Something went wrong\"}]}", errorResponse.getBody());
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
	}
	
	@Test
	public void testHandleRuntimeException_nested() throws JsonProcessingException {
		RuntimeException e = new RuntimeException(new RuntimeException("Something went wrong"));
		ResponseEntity<String> errorResponse = runtimeExceptionHandler.handleRuntimeException(e);
		
		Assert.assertEquals("{\"errors\":[{\"id\":\"Unexpected\",\"message\":\"Something went wrong\"}]}", errorResponse.getBody());
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
	}

}
