package com.att.developer.advice;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;

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
		
		ResponseEntity<ServerSideErrors> errorResponse = serverSideExceptionHandler.handleServerSideException(e);
		
		assertThat(errorResponse.getBody().getErrorColl(), hasItem(error));
		Assert.assertEquals(HttpStatus.BAD_REQUEST, errorResponse.getStatusCode());
	}

}
