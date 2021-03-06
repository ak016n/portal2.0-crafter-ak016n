package com.att.developer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogPost;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.BlogService;
import com.att.developer.util.Constants;
import com.att.developer.util.CookieUtil;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;

/**
 * Community Gateway Controller - used for Blog and Forums 
 */
@RestController
@RequestMapping("/comgw")
@Api(value = "/community", description = "Community - Blog and Forums")
public class CommunityGatewayController {
	
    public static final String FIRST_NAME = "first_name";
	public static final String LOGIN = "login";

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
	public BlogComment postComment(@PathVariable("postId") String postId, HttpServletRequest request, @RequestBody String comment) {
		
		if(StringUtils.isBlank(postId) || StringUtils.isBlank(comment)) {
			ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Missing required data post id or comments.").build();
			throw new ServerSideException(error);
		}
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		String login = portalCookieMap.get(CookieUtil.PORTAL_LOGIN);
		
		if(StringUtils.isBlank(login)) {
			ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Missing authentication").build();
			throw new ServerSideException(error);			
		}
		
		String transactionId = java.util.UUID.randomUUID().toString();
		BlogComment postCreateComment = blogService.createComment(postId, comment, login, transactionId);

		return postCreateComment;
	}
	
	@RequestMapping(value="/posts/{postId}/comments", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<BlogComment> getComments(@PathVariable("postId") String postId) {
		
		if(StringUtils.isBlank(postId)) {
			ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Missing required data post id.").build();
			throw new ServerSideException(error);
		}
		
		return blogService.getComments(postId);
	}

	@RequestMapping(value="/posts/{postId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public BlogPost getBlog(@PathVariable("postId") String postId) {
		
		if(StringUtils.isBlank(postId)) {
			ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Missing required data post id.").build();
			throw new ServerSideException(error);
		}
		
		return blogService.getBlog(postId);
	}

	@ApiOperation(value="List of blog posts")
	@RequestMapping(value="/posts", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<List<BlogPost>> getBlogs(
			@ApiParam(name="filter", value="pass in filter conditions") 
			@RequestParam MultiValueMap<String, String> allRequestParams) {
		return blogService.getBlogs(allRequestParams);
	}
	
	@RequestMapping(value="/categories", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getCategories() {
		return blogService.getCategories();
	}
	
	@RequestMapping(value="/tags", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<String> getTags() {
		return blogService.getTags();
	}
	
	@RequestMapping(value="/users", method = RequestMethod.GET)
	public Map<String, String> getUser(HttpServletRequest request) {
		
		Map<String,String> portalCookieMap = cookieUtil.getPortalUserMap(request.getCookies());
		Map<String, String> userMap = new HashMap<>();
		
		userMap.put(LOGIN, portalCookieMap.get(CookieUtil.PORTAL_LOGIN));
		userMap.put(FIRST_NAME, portalCookieMap.get(CookieUtil.PORTAL_USER));
		return userMap;
	}
	
}
