package com.att.developer.advice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.fasterxml.jackson.core.JsonProcessingException;

public class ServerSideExceptionHandlerTest {

	ServerSideExceptionHandler serverSideExceptionHandler = null;
	
	@Before
	public void init() {
		serverSideExceptionHandler = new ServerSideExceptionHandler();
	}
	
	@Test
	public void testHandleServerSideException() throws JsonProcessingException {
		ServerSideErrors serverSideErrors = new ServerSideErrors();
		ServerSideError error = new ServerSideError.Builder().id("error-101").message("things sometimes don't work").build();
		serverSideErrors.add(error);
		ServerSideException e = new ServerSideException(serverSideErrors);
		
		ResponseEntity<String> errorResponse = serverSideExceptionHandler.handleServerSideException(e);
		
		Assert.assertEquals("{\"errors\":[{\"id\":\"error-101\",\"message\":\"things sometimes don't work\"}]}", errorResponse.getBody());
		Assert.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
	}

}
