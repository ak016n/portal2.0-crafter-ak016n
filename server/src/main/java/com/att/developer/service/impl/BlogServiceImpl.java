package com.att.developer.service.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.BlogUser;
import com.att.developer.bean.User;
import com.att.developer.service.BlogService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

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
	
	public HttpHeaders getDefaultHttpHeaders() {
		HttpHeaders defaultHttpHeaders = new HttpHeaders();
		defaultHttpHeaders.add("Authorization", "Basic " + getBlogAdminPermission());
		return defaultHttpHeaders;
	}
	
	@PostConstruct
	public void init() {
    	blogHost = globalScopedParamService.get("blog_host", "http://141.204.193.91/wp-json/");
    	blogCommentStatus = globalScopedParamService.get("blog_comment_status", "approved");
    	blogAdminPermission = globalScopedParamService.get("blog_admin_permission", "YWRtaW46cGFzc3dvcmQxMjM=");
	}

	/* (non-Javadoc)
	 * @see com.att.developer.service.impl.BlogService#createComment(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public void createComment(String postId, String comment, String login) {
    	if(! doesUserExistOnBlogSite(login)) {
    		createUser(login);
    	}
    }
	
	public void proxyCreateComment(String postId, String comment, String login) {
		String uri = getBlogHost() + "users/" + login;
		restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(null, getDefaultHttpHeaders()), Map.class);
	
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
				// returns status as false
			} else {
				logger.error(e);
				throw new RuntimeException(e);
			}
		}
		return status;
	}

	/* (non-Javadoc)
	 * @see com.att.developer.service.impl.BlogService#createUser(java.lang.String)
	 */
	@Override
	public boolean createUser(String login) {
		boolean creationStatus = false;
		
		User user = userProfileService.getUser(login);
		BlogUser blogUser = new BlogUser(user);
		
		String uri = getBlogHost() + "users";
		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Basic " + getBlogAdminPermission());
		
		try {
			@SuppressWarnings("rawtypes")
			ResponseEntity<Map> responseEntity = restTemplate.exchange(getURI(uri), HttpMethod.POST, new HttpEntity<>(blogUser, headers), Map.class);
			if(responseEntity.getStatusCode().is2xxSuccessful()){
				creationStatus = true;
			}
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return creationStatus;
	}
	
	
}
