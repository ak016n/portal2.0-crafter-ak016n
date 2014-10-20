package com.att.developer.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.ContentService;
import com.att.developer.service.EventTrackingService;
import com.att.developer.util.CookieUtil;

@RestController
@RequestMapping("/cg")
public class ContentGatewayController {
	
	@Inject
	private EventTrackingService eventTrackingService;
	
    @Inject
    private CookieUtil cookieUtil;
    
    @Inject
    private ContentService contentService;
	
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	@RequestMapping(value="/{url}", method = RequestMethod.GET)
	public String getProperty(@PathVariable("url") String url, HttpServletRequest request) throws UnsupportedEncodingException {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(url)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing required path arguments.").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		System.out.println("PORTAL_LOGIN  = " + portalCookieMap.get(cookieUtil.PORTAL_LOGIN));
		
		Map contentResponse = contentService.getContent("sample", "somas");
		System.out.println("Content Response : " + contentResponse);
			
		return null;
	}
	


}
