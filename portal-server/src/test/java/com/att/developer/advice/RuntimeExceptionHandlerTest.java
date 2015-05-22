package com.att.developer.advice;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
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
		ResponseEntity<ServerSideErrors> errorResponse = runtimeExceptionHandler.handleRuntimeException(e);
		
		assertThat(errorResponse.getBody().getErrorColl(), hasItem(new ServerSideError.Builder().id("Unexpected").message("Something went wrong").build()));
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
	}
	
	@Test
	public void testHandleRuntimeException_nested() throws JsonProcessingException {
		RuntimeException e = new RuntimeException(new RuntimeException("Something went wrong"));
		ResponseEntity<ServerSideErrors> errorResponse = runtimeExceptionHandler.handleRuntimeException(e);
		
		assertThat(errorResponse.getBody().getErrorColl(), hasItem(new ServerSideError.Builder().id("Unexpected").message("Something went wrong").build()));
		
		Assert.assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, errorResponse.getStatusCode());
	}

}
