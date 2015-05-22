package com.att.developer.service.impl;

import java.net.URI;
import java.util.Arrays;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

public class ContentServiceImplTest {
	
    @Mock
    private RestTemplate mockRestTemplate;
    
    @Mock
    private GlobalScopedParamService mockGlobalScopedParamService;
    
    @Mock
    private UserProfileService mockUserProfileService;

    private ContentServiceImpl contentService;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	
    	contentService = new ContentServiceImpl();
    	contentService.setGlobalScopedParamService(mockGlobalScopedParamService);
    	contentService.setUserProfileService(mockUserProfileService);
    	contentService.setRestTemplate(mockRestTemplate);
    }
    
	@Test
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void testGetContent() {
		
		Mockito.when(mockGlobalScopedParamService.get(Mockito.anyString(), Mockito.anyString())).thenReturn("key");
		Mockito.when(mockUserProfileService.getUserPermissions("somas")).thenReturn(Arrays.asList(new String[] {"admin"}));
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(new HashMap());
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		
		Assert.assertNotNull(contentService.getContent("xyz", "somas"));
		
		Mockito.verify(mockRestTemplate, Mockito.times(1)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}

	@Test
	public void testGetContent_nullContent() {
		
		Mockito.when(mockGlobalScopedParamService.get(Mockito.anyString(), Mockito.anyString())).thenReturn("key");
		Mockito.when(mockUserProfileService.getUserPermissions("somas")).thenReturn(Arrays.asList(new String[] {"admin"}));
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(null);
		
		Assert.assertNull(contentService.getContent("xyz", "somas"));
		
		Mockito.verify(mockRestTemplate, Mockito.times(1)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}
	
}
