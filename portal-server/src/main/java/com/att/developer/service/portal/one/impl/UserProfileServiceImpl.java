package com.att.developer.service.portal.one.impl;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.User;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;
import com.att.developer.util.Constants;

@Component
public class UserProfileServiceImpl implements UserProfileService {

	private final Logger logger = LogManager.getLogger();
	
    @Inject
    private RestTemplate restTemplate;
    
    @Inject
    private GlobalScopedParamService globalScopedParamService;
    
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}

	@Override
	public List<String> getUserPermissions(String login) {
		
		String portalHost = globalScopedParamService.get("portal_one_host", "localhost");
		
		String key = globalScopedParamService.get("portal_one_key", "DEVPORTAL");
		String secret = globalScopedParamService.get("portal_one_secret", "devPortalPassword");
		String vendor = globalScopedParamService.get("portal_one_vendor", "DEVPORTAL");
		
		String authorizationToken = getSystemAuthorizationToken(portalHost, key, secret, vendor);
		
		List<String> principalColl = new ArrayList<String>();
		if(authorizationToken != null) {
			principalColl = fetchUserPermissions(portalHost, authorizationToken, vendor, login);
		}
		
		return principalColl;
	}
	
	@SuppressWarnings({ "rawtypes"})
	public String getSystemAuthorizationToken(String portalHost, String key, String secret, String vendor) {
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     
		body.add(Constants.KEY, key);
		body.add(Constants.SECRET, secret);
		
		HttpHeaders authHeaders = getBasicAuthHeaders(vendor);
		
		ResponseEntity<Map> authResponse = null;
		
		try {
			String uri = Constants.HTTPS_URL + portalHost + "/developer/rest/user/system/token";
			authResponse = restTemplate.exchange(new URI(uri), HttpMethod.POST, new HttpEntity<>(body, authHeaders), Map.class);
			logReturnStatus(authResponse, "uri : " + uri);
		} catch (Exception e) {
			logger.error(e);
			ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Error fetching system token from portal").build();
			throw new ServerSideException(e, error);
		}

		String authorizationToken = null;
				
		if(authResponse != null && authResponse.getBody() != null) {
			authorizationToken = (String) authResponse.getBody().get("authorizationToken");
		}
		return authorizationToken;
	}

	private HttpHeaders getBasicAuthHeaders(String vendor) {
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add(Constants.X_AUTH_VENDOR_HEADER, vendor);
		authHeaders.add(Constants.ACCEPT_HEADER, Constants.VERSION_1);
		return authHeaders;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public List<String> fetchUserPermissions(String portalHost, String authorizationToken, String vendor, String login) {
		
		HttpHeaders authHeaders = getBasicAuthHeaders(vendor);
		authHeaders.add(Constants.AUTHORIZATION_TOKEN, authorizationToken);
		
		ResponseEntity<List> principalResponse = null;
		
		if(StringUtils.isNotBlank(login)) {
			try {
				String uri = Constants.HTTPS_URL + portalHost + "/developer/rest/user/principal/" + login;
				principalResponse = restTemplate.exchange(new URI(uri), HttpMethod.GET, new HttpEntity<>(null, authHeaders), List.class);
				logReturnStatus(principalResponse, "uri : " + uri);
			} catch (Exception e) {
				logger.error(e);
				ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Error fetching user principal details for User: " + login).build();
				throw new ServerSideException(e, error);
			}
		}

		List<String> principalColl = new ArrayList<String>();
		
		if(principalResponse != null && principalResponse.getBody() != null && principalResponse.getBody() != null) {
			principalColl = (List<String>) principalResponse.getBody();
		}
		return principalColl;
	}

	// In case of error, log the response with additional information for investigation
	private void logReturnStatus(ResponseEntity<? extends Object> response, String comments) {
		if(response != null && !response.getStatusCode().is2xxSuccessful()) {
			logger.error(comments, response);
		}
	}

	@Override
	public User getUser(String login) {
		String portalHost = globalScopedParamService.get("portal_one_host", "localhost");
		String key = globalScopedParamService.get("portal_one_key", "DEVPORTAL");
		String secret = globalScopedParamService.get("portal_one_secret", "devPortalPassword");
		String vendor = globalScopedParamService.get("portal_one_vendor", "DEVPORTAL");
		
		String authorizationToken = getSystemAuthorizationToken(portalHost, key, secret, vendor);
		
		User user = null;
		if(authorizationToken != null) {
			user = fetchUser(portalHost, authorizationToken, vendor, login);
		}
		
		return user;
	}
	
	private User fetchUser(String portalHost, String authorizationToken, String vendor, String login) {
		
		HttpHeaders authHeaders = getBasicAuthHeaders(vendor);
		authHeaders.add(Constants.AUTHORIZATION_TOKEN, authorizationToken);
		
		ResponseEntity<User> userResponse = null;
		
		if(StringUtils.isNotBlank(login)) {
			try {
				String uri = Constants.HTTPS_URL + portalHost + "/developer/rest/user/" + login;
				userResponse = restTemplate.exchange(new URI(uri), HttpMethod.GET, new HttpEntity<>(null, authHeaders), User.class);
				logReturnStatus(userResponse, "uri : " + uri);
			} catch (Exception e) {
				logger.error(e);
				ServerSideError error = new ServerSideError.Builder().id(Constants.SS_GENERAL_ERROR_ID).message("Error fetching user details for User: " + login).build();
				throw new ServerSideException(e, error);
			}
		}

		return userResponse.getBody();
	}

}
