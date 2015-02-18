package com.att.developer.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.User;
import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogCreateUser;
import com.att.developer.bean.blog.BlogError;
import com.att.developer.bean.blog.BlogPost;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.BlogService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BlogServiceImpl implements BlogService {

	private final Logger logger = LogManager.getLogger();
	
	private String blogHost;
	private String blogCommentStatus;
	private String blogAdminPermission;
	
    @Inject
    private RestTemplate restTemplate;
    
    @Inject
    private GlobalScopedParamService globalScopedParamService;
    
    @Inject
    private UserProfileService userProfileService;
    
    public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}

	public void setUserProfileService(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}
	
	public String getBlogHost() {
		return blogHost;
	}

	public String getBlogCommentStatus() {
		return blogCommentStatus;
	}

	public String getBlogAdminPermission() {
		return blogAdminPermission;
	}
	
	@PostConstruct
	public void init() {
    	blogHost = globalScopedParamService.get("blog_host", "http://141.204.193.91/wp-json/");
    	blogCommentStatus = globalScopedParamService.get("blog_comment_status", "approved");
    	blogAdminPermission = globalScopedParamService.get("blog_admin_permission", "YWRtaW46cGFzc3dvcmQxMjM=");
	}

	@Override
	public BlogComment createComment(String postId, String comment, String login) {
    	if(! doesUserExistOnBlogSite(login)) {
    		createUser(login);
    	}
    	return proxyCreateComment(postId, comment, login);
    }
	
	private boolean doesUserExistOnBlogSite(String login) {
		boolean status = false;
		String uri = getBlogHost() + "users/" + login;
		
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = null;
		
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.GET, new HttpEntity<>(null, getDefaultHttpHeaders()), Map.class);
			
			if(responseEntity.getStatusCode().is2xxSuccessful()) {
				status = true;
			}
		} catch (RestClientException e) {
			if(e instanceof HttpClientErrorException && StringUtils.contains(((HttpClientErrorException)e).getResponseBodyAsString(), "json_user_invalid_username")) {
				// OK to swallow exception - we want it to return status as false in this scenario
			} else {
				logger.error(e);
				throw new RuntimeException(e);
			}
		}
		return status;
	}

	@Override
	public boolean createUser(String login) {
		boolean creationStatus = false;
		
		User user = userProfileService.getUser(login);
		BlogCreateUser blogUser = new BlogCreateUser(user);
		
		String uri = getBlogHost() + "users";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getBlogAdminPermission());
		
		try {
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(blogUser, headers), Map.class);
			if(responseEntity.getStatusCode().is2xxSuccessful()) {
				creationStatus = true;
			}
		}  catch (HttpClientErrorException | HttpServerErrorException e) {
			extractErrorInfoAndThrowEx(e);
		}  catch (RestClientException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		
		return creationStatus;
	}
	
	public BlogComment proxyCreateComment(String postId, String data, String login) {
		String uri = getBlogHost() + "posts/" + postId + "/comments";
		
		ResponseEntity<BlogComment> responseEntity = null;
		
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(data, getUserAuthHttpHeaders(login)), BlogComment.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			extractErrorInfoAndThrowEx(e);
		} catch (RestClientException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		return responseEntity.getBody();
	}

	private void extractErrorInfoAndThrowEx(HttpStatusCodeException e) {
		String errorResponse = e.getResponseBodyAsString();
		
		if(StringUtils.isBlank(errorResponse)) {
			throw new RuntimeException(e);
		} else {
				BlogError blogError = getBlogError(errorResponse, e.getResponseBodyAsByteArray());
				ServerSideError error = new ServerSideError.Builder().id(blogError.getCode()).message(blogError.getMessage()).build();
				throw new ServerSideException(error);
		}
	}

	private BlogError getBlogError(String errorResponse, byte[] bs) {
		BlogError blogError = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (StringUtils.contains(errorResponse, "[") && StringUtils.contains(errorResponse, "]")) {
				BlogError[] blogErrorArr;
				blogErrorArr = mapper.readValue(bs, BlogError[].class);
				blogError = blogErrorArr[0];
			} else {
				blogError = mapper.readValue(bs, BlogError.class);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return blogError;
	}

	private HttpHeaders getDefaultHttpHeaders() {
		HttpHeaders defaultHttpHeaders = new HttpHeaders();
		defaultHttpHeaders.add("Authorization", "Basic " + getBlogAdminPermission());
		return defaultHttpHeaders;
	}
	
	private HttpHeaders getUserAuthHttpHeaders(String login) {
		HttpHeaders userAuthHeaders = new HttpHeaders();
		userAuthHeaders.add("Authorization", "Basic " + Base64.encodeBase64String((login + ":password123").getBytes()));
		return userAuthHeaders;
	}

	private URI getURI(String uri) {
		URI tempUri = null;
		try {
			tempUri = new URI(uri);
		} catch (URISyntaxException e) {
			logger.error(e);
			throw new RuntimeException("Unable to convert url into URI : " + uri, e);
		}
		return tempUri;
	}
	
	//Its string instead of list of blog posts because we are using this a proxy
	public List<BlogComment> getComments(String postId) {
		String uri = getBlogHost() + "posts/" + postId + "/comments";
		ParameterizedTypeReference<List<BlogComment>> typeRef = new ParameterizedTypeReference<List<BlogComment>>() {};
		
		ResponseEntity<List<BlogComment>> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.GET, null, typeRef);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			extractErrorInfoAndThrowEx(e);
		} catch (RestClientException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		return responseEntity.getBody();
    }

	//Its string instead of list of blog posts because we are using this a proxy
	public BlogPost getBlog(String postId) {
		String uri = getBlogHost() + "posts/" + postId;
		
		ResponseEntity<BlogPost> responseEntity = null;
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.GET, null, BlogPost.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			extractErrorInfoAndThrowEx(e);
		} catch (RestClientException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		
		return responseEntity.getBody();
    }


}
