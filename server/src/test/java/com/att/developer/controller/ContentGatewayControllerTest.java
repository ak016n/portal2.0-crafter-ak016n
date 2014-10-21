package com.att.developer.controller;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.exception.ServerSideException;
import com.att.developer.service.ContentService;
import com.att.developer.util.CookieUtil;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class ContentGatewayControllerTest {
	
    @Mock
    private CookieUtil mockCookieUtil;
    
    @Mock
    private ContentService mockContentService;
	
    ContentGatewayController contentGatewayController;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	
    	contentGatewayController = new ContentGatewayController();
    	
    	contentGatewayController.setContentService(mockContentService);
    	contentGatewayController.setCookieUtil(mockCookieUtil);
    }
    
	@Test
	public void getContent_empty() throws UnsupportedEncodingException {
		HttpServletRequest mockServletRequest = Mockito.mock(HttpServletRequest.class);
		
		Map response = contentGatewayController.getContent("sample", mockServletRequest);
		
		Assert.assertNotNull(response);
		Assert.assertTrue(response.isEmpty());
	}
	
	@Test(expected=ServerSideException.class)
	public void getContent_nullURL() throws UnsupportedEncodingException {
		HttpServletRequest mockServletRequest = Mockito.mock(HttpServletRequest.class);
		
		contentGatewayController.getContent(null, mockServletRequest);
	}

	@Test
	public void getContent_emptyCookie() throws UnsupportedEncodingException {
		HttpServletRequest mockServletRequest = Mockito.mock(HttpServletRequest.class);
		
		Map contentMap = new HashMap();
		contentMap.put("x", "y");
		
		Mockito.when(mockContentService.getContent("sample", null)).thenReturn(contentMap);
		Map response = contentGatewayController.getContent("sample", mockServletRequest);
		
		Assert.assertNotNull(response);
		Assert.assertEquals("y", response.get("x"));
	}
	
	@Test
	public void getContent() throws UnsupportedEncodingException {
		HttpServletRequest mockServletRequest = Mockito.mock(HttpServletRequest.class);
		
		Map contentMap = new HashMap();
		contentMap.put("page", "admin content");
		
		Map cookieMap = new HashMap();
		cookieMap.put("portal_login", "somas");
		
		Mockito.when(mockServletRequest.getCookies()).thenReturn(new Cookie[] {new Cookie("user", "somas")});
		Mockito.when(mockCookieUtil.getPortalUserMap(Mockito.any(Cookie[].class))).thenReturn(cookieMap);
		Mockito.when(mockContentService.getContent("sample", "somas")).thenReturn(contentMap);
		Map response = contentGatewayController.getContent("sample", mockServletRequest);
		
		Assert.assertNotNull(response);
		Assert.assertEquals("admin content", response.get("page"));
		Mockito.verify(mockCookieUtil, Mockito.times(1)).getPortalUserMap(Mockito.any(Cookie[].class));
		Mockito.verify(mockContentService, Mockito.times(1)).getContent(Mockito.anyString(), Mockito.anyString());
	}
}
