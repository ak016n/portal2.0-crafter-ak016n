package com.att.developer.util;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.fasterxml.jackson.core.JsonProcessingException;

public class FaultUtilsTest {
	
	@Mock
	BindingResult mockBindingResult;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void testCheckForViolations_true() {
		Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);
		Assert.assertTrue(FaultUtils.checkForViolations(false, mockBindingResult, new ServerSideErrors()));
	}
	
	@Test
	public void testCheckForViolations_false() {
		Mockito.when(mockBindingResult.hasErrors()).thenReturn(false);
		Assert.assertFalse(FaultUtils.checkForViolations(false, mockBindingResult, new ServerSideErrors()));
	}
	
	@Test
	public void testCheckForViolations_passThru() {
		Mockito.when(mockBindingResult.hasErrors()).thenReturn(false);
		Assert.assertTrue(FaultUtils.checkForViolations(true, mockBindingResult, new ServerSideErrors()));
	}

	@Test
	public void testProcessBindingError() {
		FieldError fieldError = new FieldError("com.att.developer.Obj", "field", "error", true, new String[] {"500"}, null, "unit needs to be validated");
		List<FieldError> errors = Arrays.asList(fieldError);
		Mockito.when(mockBindingResult.getFieldErrors()).thenReturn(errors);
		
		ServerSideErrors errorColl = new ServerSideErrors();
		FaultUtils.processBindingError(mockBindingResult, errorColl);
		Assert.assertEquals(errorColl.getErrorColl().get(0).getId(), "com.att.developer.Obj.field.id");
		Assert.assertEquals(errorColl.getErrorColl().get(0).getMessage(), "com.att.developer.Obj.field.500.message");
	}

	@Test
	public void testConvertToJson() throws JsonProcessingException {
		ServerSideError error = new ServerSideError.Builder().id("id").message("message").build();
		ServerSideErrors errorColl = new ServerSideErrors();
		errorColl.add(error);
		
		Assert.assertEquals("{\"errors\":[{\"id\":\"id\",\"message\":\"message\"}]}", FaultUtils.convertToJson(errorColl));
	}
	
	@Test
	public void testConvertFirstLetterToCaps() {
		Assert.assertEquals("500", FaultUtils.convertFirstLetterToCaps("500"));
		Assert.assertEquals("Abc", FaultUtils.convertFirstLetterToCaps("abc"));
		Assert.assertEquals(null, FaultUtils.convertFirstLetterToCaps(null));
	}

	@Test
	public void testGetFilteredText() {
		Assert.assertEquals("contacts", FaultUtils.getFilteredText("contacts[]"));
		Assert.assertEquals("contacts", FaultUtils.getFilteredText("contacts[0]"));
	}

}
