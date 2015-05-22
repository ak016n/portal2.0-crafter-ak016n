package com.att.developer.service.portal.one.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.service.GlobalScopedParamService;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class UserProfileServiceImplTest {
	
    @Mock
    private RestTemplate mockRestTemplate;
    
    @Mock
    private GlobalScopedParamService mockGlobalScopedParamService;
    
    private UserProfileServiceImpl userProfileServiceImpl;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	
    	userProfileServiceImpl = new UserProfileServiceImpl();
    	userProfileServiceImpl.setGlobalScopedParamService(mockGlobalScopedParamService);
    	userProfileServiceImpl.setRestTemplate(mockRestTemplate);
    }
    
	@Test
	public void testGetUserPermissions_success() throws RestClientException, URISyntaxException {
		Mockito.when(mockGlobalScopedParamService.get(Mockito.anyString(), Mockito.anyString())).thenReturn("junk");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authorizationToken", "token");
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(map);
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		ResponseEntity response2 = Mockito.mock(ResponseEntity.class);
		Mockito.when(response2.getBody()).thenReturn(Arrays.asList(new String[] {"admin"}));
		Mockito.when(response2.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.eq(new URI("https://junk/developer/rest/user/system/token")), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		Mockito.when(mockRestTemplate.exchange(Mockito.eq(new URI("https://junk/developer/rest/user/principal/somas")), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response2);
		List<String> principalColl = userProfileServiceImpl.getUserPermissions("somas");
		
		Assert.assertNotNull(principalColl);
		Assert.assertEquals(principalColl.get(0), "admin");
		Mockito.verify(mockRestTemplate, Mockito.times(2)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}
	
	@Test
	public void testGetUserPermissions_nullToken() {
		Mockito.when(mockGlobalScopedParamService.get(Mockito.anyString(), Mockito.anyString())).thenReturn("junk");
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authorizationToken", null);
		map.put("authorities", Arrays.asList(new String[] {"admin"}));
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(map);
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		List<String> principalColl = userProfileServiceImpl.getUserPermissions("somas");
		
		Assert.assertNotNull(principalColl);
		Assert.assertTrue(principalColl.isEmpty());
		Mockito.verify(mockRestTemplate, Mockito.times(1)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}

	@Test
	public void testGetSystemAuthorizationToken() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authorizationToken", "token");
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(map);
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		String authToken = userProfileServiceImpl.getSystemAuthorizationToken("local", "key", "secret", "vendor");
		
		Assert.assertNotNull(authToken);
		Assert.assertEquals("token", authToken);
	}
	
	@Test
	public void testGetSystemAuthorizationToken_error() {
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(null);
		String authToken = userProfileServiceImpl.getSystemAuthorizationToken("local", "key", "secret", "vendor");
		
		Assert.assertNull(authToken);
	}

	@Test(expected=RuntimeException.class)
	public void testGetSystemAuthorizationToken_exception() {
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenThrow(new RuntimeException());
		userProfileServiceImpl.getSystemAuthorizationToken("local", "key", "secret", "vendor");
	}
	
	@Test
	public void testFetchUserPermissions() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authorizationToken", "token");
		map.put("authorities", Arrays.asList(new String[] {"admin"}));
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(map.get("authorities"));
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		List<String> principalColl = userProfileServiceImpl.fetchUserPermissions("local", "token", "vendor", "somas");
		
		Assert.assertNotNull(principalColl);
		Assert.assertEquals("admin", principalColl.get(0));
		Mockito.verify(mockRestTemplate, Mockito.times(1)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}
	
	
	@Test(expected=RuntimeException.class)
	public void testFetchUserPermissions_exception() {
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenThrow(new RuntimeException());
		userProfileServiceImpl.fetchUserPermissions("local", "token", "vendor", "somas");
	}
	
	@Test
	public void testFetchUserPermissions_null() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("authorizationToken", "token");
		map.put("authorities", null);
		
		ResponseEntity response = Mockito.mock(ResponseEntity.class);
		Mockito.when(response.getBody()).thenReturn(map.get("authorities"));
		Mockito.when(response.getStatusCode()).thenReturn(HttpStatus.OK);
		
		Mockito.when(mockRestTemplate.exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class))).thenReturn(response);
		List<String> principalColl = userProfileServiceImpl.fetchUserPermissions("local", "token", "vendor", "somas");
		
		Assert.assertNotNull(principalColl);
		Assert.assertTrue(principalColl.isEmpty());
		Mockito.verify(mockRestTemplate, Mockito.times(1)).exchange(Mockito.any(URI.class), Mockito.any(HttpMethod.class), Mockito.any(HttpEntity.class), (Class<?>)Mockito.any(Class.class));
	}

}
