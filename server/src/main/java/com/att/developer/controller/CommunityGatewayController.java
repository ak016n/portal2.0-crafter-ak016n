package com.att.developer.controller;

import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.BlogService;
import com.att.developer.util.CookieUtil;

/**
 * Community Gateway Controller - used for Blog and Forums 
 */
@RestController
@RequestMapping("/comgw")
public class CommunityGatewayController {
	
	private final Logger logger = LogManager.getLogger();
	
    @Inject
    private CookieUtil cookieUtil;
    
    @Inject
    private BlogService blogService;
	
	public void setCookieUtil(CookieUtil cookieUtil) {
		this.cookieUtil = cookieUtil;
	}

	public void setBlogService(BlogService blogService) {
		this.blogService = blogService;
	}

	@RequestMapping(value="/posts/{postId}/comments", method = RequestMethod.POST)
	public void postComment(@PathVariable("postId") String postId, HttpServletRequest request, @RequestBody String comment) throws UnsupportedEncodingException {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(postId)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing required POST Id.").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		logger.debug("PORTAL_LOGIN  = " + portalCookieMap.get(CookieUtil.PORTAL_LOGIN));
		
		blogService.createComment(postId, comment, portalCookieMap.get(CookieUtil.PORTAL_LOGIN));
		//logger.debug("Content Response : " + contentResponse);
			
		//return contentResponse;
	}
	


}
