package com.att.developer.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.exception.MessageProcessingException;

import freemarker.core.ParseException;
import freemarker.template.Configuration;
import freemarker.template.MalformedTemplateNameException;
import freemarker.template.Template;
import freemarker.template.TemplateNotFoundException;

public class MessageUtilsTest {

	@Mock
	private Configuration mockFreeMarkerConfiguration;
	
	MessageUtils messageUtils;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		messageUtils = new MessageUtils();
		
		messageUtils.setFreeMarkerConfiguration(mockFreeMarkerConfiguration);
	}
	
	@Test
	public void testGetMessage_happyPath() throws MessageProcessingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		Map<String, Object> mapOfConfig = new HashMap<>();
		mapOfConfig.put("x", "y");
		
		Template template = new Template("hello_world", new StringReader("${x}"), null);
		Mockito.when(mockFreeMarkerConfiguration.getTemplate("hello_world")).thenReturn(template);
		String finalOutput = messageUtils.getMessage("hello_world", mapOfConfig);
		
		Assert.assertNotNull(finalOutput);
		Assert.assertEquals(finalOutput, "y");
	}
	
	@Test(expected=MessageProcessingException.class)
	public void testGetMessage_templateNotFound() throws MessageProcessingException, TemplateNotFoundException, MalformedTemplateNameException, ParseException, IOException {
		Mockito.when(mockFreeMarkerConfiguration.getTemplate("hello_world")).thenThrow(new TemplateNotFoundException("hello_world", null, "not found"));
		messageUtils.getMessage("hello_world", null);
	}

}
