package com.att.developer.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;

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

import com.att.developer.bean.EventLog;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.User;
import com.att.developer.bean.blog.BlogComment;
import com.att.developer.bean.blog.BlogCreateUser;
import com.att.developer.bean.blog.BlogError;
import com.att.developer.bean.blog.BlogPost;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.BlogService;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;
import com.att.developer.util.StringBuilderUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class BlogServiceImpl implements BlogService {

	private static final String _CLOSE_SQUARE_BRACKET = "]";
	private static final String _OPEN_SQUARE_BRACKET = "[";
	private static final String PASSWORD123 = ":password123";
	private static final String JSON_USER_INVALID_USERNAME = "json_user_invalid_username";
	public static final String DEFAULT_BLOG_HOST = "http://devpgm-wcm-stage.eng.mobilephone.net/blog/wp-json/";
	private static final String DEFAULT_COMMENT_STATUS = "approved";
	private static final String DEFAULT_BLOG_ADMIN_AUTH = "YWRtaW46cGFzc3dvcmQxMjM=";
	private static final String BLOG_ADMIN_AUTH_KEY = "blog_admin_permission";
	private static final String BLOG_COMMENT_STATUS_KEY = "blog_comment_status";
	public static final String BLOG_HOST_KEY = "blog_host";
	private static final String COMMENTS_PATH = "/comments";
	private static final String POSTS_PATH = "posts/";
	private static final String USERS_PATH = "users/";
	private static final String AUTHORIZATION = "Authorization";
	private static final String BASIC = "Basic ";

	private final Logger logger = LogManager.getLogger();
	
    @Inject
    private RestTemplate restTemplate;
    
    @Inject
    private GlobalScopedParamService globalScopedParamService;
    
    @Inject
    private UserProfileService userProfileService;
    
	@Inject
	private EventTrackingService eventTrackingService;
    
    public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}

	public void setUserProfileService(UserProfileService userProfileService) {
		this.userProfileService = userProfileService;
	}
	
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}
	
	public String getBlogHost() {
		return globalScopedParamService.get(BLOG_HOST_KEY, DEFAULT_BLOG_HOST);
	}

	public String getBlogCommentStatus() {
		return globalScopedParamService.get(BLOG_COMMENT_STATUS_KEY, DEFAULT_COMMENT_STATUS);
	}

	public String getBlogAdminAuth() {
		return globalScopedParamService.get(BLOG_ADMIN_AUTH_KEY, DEFAULT_BLOG_ADMIN_AUTH);
	}
	
	@Override
	public BlogComment createComment(String postId, String comment, String login, String transactionId) {
    	if(! doesUserExistOnBlogSite(login)) {
    		createUser(login, transactionId);
    	}
    	return proxyCreateComment(postId, comment, login, transactionId);
    }
	
	private boolean doesUserExistOnBlogSite(String login) {
		boolean status = false;
		String uri = StringBuilderUtil.concatString(getBlogHost() , USERS_PATH , login);
		
		@SuppressWarnings("rawtypes")
		ResponseEntity<Map> responseEntity = null;
		
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.GET, new HttpEntity<>(null, getDefaultHttpHeaders()), Map.class);
			
			if(responseEntity.getStatusCode().is2xxSuccessful()) {
				status = true;
			}
		} catch (RestClientException e) {
			if(e instanceof HttpClientErrorException && StringUtils.contains(((HttpClientErrorException)e).getResponseBodyAsString(), JSON_USER_INVALID_USERNAME)) {
				// OK to swallow exception - we want it to return status as false in this scenario
			} else {
				logger.error(e);
				throw new RuntimeException(e);
			}
		}
		return status;
	}

	@Override
	public boolean createUser(String login, String transactionId) {
		boolean creationStatus = false;
		
		User user = userProfileService.getUser(login);
		BlogCreateUser blogUser = new BlogCreateUser(user);
		
		String uri = StringBuilderUtil.concatString(getBlogHost() , USERS_PATH);
		HttpHeaders headers = new HttpHeaders();
		headers.add(AUTHORIZATION, BASIC + getBlogAdminAuth());
		
		try {
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(blogUser, headers), Map.class);
			eventTrackingService.writeEvent(new EventLog(login, null, null, EventType.BLOG_USER_CREATED, null, ActorType.DEV_PROGRAM_USER, transactionId));
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
	
	public BlogComment proxyCreateComment(String postId, String data, String login, String transactionId) {
		String uri = StringBuilderUtil.concatString(getBlogHost() , POSTS_PATH , postId , COMMENTS_PATH);
		
		ResponseEntity<BlogComment> responseEntity = null;
		
		try {
			responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(data, getUserAuthHttpHeaders(login)), BlogComment.class);
		} catch (HttpClientErrorException | HttpServerErrorException e) {
			extractErrorInfoAndThrowEx(e);
		} catch (RestClientException e) {
			logger.error(e);
			throw new RuntimeException(e);
		}
		BlogComment blogComment = responseEntity.getBody();
		eventTrackingService.writeEvent(new EventLog(login, null, null, EventType.BLOG_COMMENT_POST, getBlogCommentId(blogComment), ActorType.DEV_PROGRAM_USER, transactionId));
		return blogComment;
	}

	private String getBlogCommentId(BlogComment blogComment) {
		String id = StringUtils.EMPTY;
		if(blogComment != null) {
			id = blogComment.getId();
		}
		return StringBuilderUtil.concatString("Comment id: ", id);
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
			if (StringUtils.contains(errorResponse, _OPEN_SQUARE_BRACKET) && StringUtils.contains(errorResponse, _CLOSE_SQUARE_BRACKET)) {
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
		defaultHttpHeaders.add(AUTHORIZATION, BASIC + getBlogAdminAuth());
		return defaultHttpHeaders;
	}
	
	private HttpHeaders getUserAuthHttpHeaders(String login) {
		HttpHeaders userAuthHeaders = new HttpHeaders();
		userAuthHeaders.add(AUTHORIZATION, BASIC + Base64.encodeBase64String((login + PASSWORD123).getBytes()));
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
	
	@Override
	public List<BlogComment> getComments(String postId) {
		String uri = StringBuilderUtil.concatString(getBlogHost(), POSTS_PATH, postId, COMMENTS_PATH);
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

	@Override
	public BlogPost getBlog(String postId) {
		String uri = StringBuilderUtil.concatString(getBlogHost() , POSTS_PATH , postId);
		
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
