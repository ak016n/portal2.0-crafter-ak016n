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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.ServerSideError;
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
	public @ResponseBody String postComment(@PathVariable("postId") String postId, HttpServletRequest request, @RequestBody String comment) throws UnsupportedEncodingException {
		
		if(StringUtils.isBlank(postId) || StringUtils.isBlank(comment)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing required data post id or comments.").build();
			throw new ServerSideException(error);
		}
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		String login = portalCookieMap.get(CookieUtil.PORTAL_LOGIN);
		
		if(StringUtils.isBlank(login)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing authentication").build();
			throw new ServerSideException(error);			
		}
		
		logger.debug("PORTAL_LOGIN  = " + login);
		
		blogService.createComment(postId, comment, login);

		return null;
	}
	


}
