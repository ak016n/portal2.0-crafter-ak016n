package com.att.developer.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.util.Constants;

public class DatabaseTemplateLoaderTest {

	private DatabaseTemplateLoader databaseTemplateLoader;
	
	@Mock
	private GlobalScopedParamService mockGlobalScopedParamService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		
		databaseTemplateLoader = new DatabaseTemplateLoader();
		databaseTemplateLoader.setGlobalScopedParamService(mockGlobalScopedParamService);
	}
	
	@Test
	public void testFindTemplateSource_happyPath() throws IOException {
		Map<String, String> template = new HashMap<>();
		template.put("a", "b");
		
		Mockito.when(mockGlobalScopedParamService.getMap(Constants.MESSAGE_TEMPLATE, "XYZ", "key")).thenReturn(template);
		
		Object value = databaseTemplateLoader.findTemplateSource("xyz->key->a");

		Assert.assertNotNull(value);
		Assert.assertEquals(value, "b");
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testFindTemplateSource_badParam() throws IOException {
		databaseTemplateLoader.findTemplateSource("xyz->key");
	}

	@Test
	public void testGetReader_happyPath() throws IOException {
		Reader reader = databaseTemplateLoader.getReader("XYZ", "UTF-8");
		Assert.assertNotNull(reader);
		Assert.assertTrue(reader instanceof StringReader);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testGetReader_notStringParam() throws IOException {
		databaseTemplateLoader.getReader(1, "UTF-8");
	}

}
;