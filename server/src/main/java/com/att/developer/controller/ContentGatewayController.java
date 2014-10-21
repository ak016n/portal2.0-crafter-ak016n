package com.att.developer.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.ContentService;
import com.att.developer.util.CookieUtil;

@RestController
@RequestMapping("/cg")
public class ContentGatewayController {
	
	private final Logger logger = LogManager.getLogger();
	
    @Inject
    private CookieUtil cookieUtil;
    
    @Inject
    private ContentService contentService;
	
	public void setCookieUtil(CookieUtil cookieUtil) {
		this.cookieUtil = cookieUtil;
	}

	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}

	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/{url}", method = RequestMethod.GET)
	public Map getContent(@PathVariable("url") String url, HttpServletRequest request) throws UnsupportedEncodingException {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(url)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing required path arguments.").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		logger.debug("PORTAL_LOGIN  = " + portalCookieMap.get(CookieUtil.PORTAL_LOGIN));
		
		Map contentResponse = contentService.getContent(url, portalCookieMap.get(CookieUtil.PORTAL_LOGIN));
		logger.debug("Content Response : " + contentResponse);
			
		return contentResponse;
	}
	


}
