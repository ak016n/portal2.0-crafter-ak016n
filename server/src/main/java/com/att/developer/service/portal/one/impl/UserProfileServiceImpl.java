package com.att.developer.service.portal.one.impl;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

@Component
public class UserProfileServiceImpl implements UserProfileService {

    @Inject
    private RestTemplate restTemplate;
    
    @Inject
    private GlobalScopedParamService globalScopedParamService;
	
	@Override
	public List<String> getUserPermissions(String login) {
		
		String portalHost = globalScopedParamService.get("portal_one_host", "localhost");
		
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();     
		body.add("key", "APIM");
		body.add("secret", "password123");
		
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
		body.add("key", key);
		body.add("secret", secret);
		
		HttpHeaders authHeaders = getBasicAuthHeaders(vendor);
		
		ResponseEntity<Map> authResponse = null;
		
		try {
			authResponse = restTemplate.exchange(new URI("https://" + portalHost + "/developer/rest/user/system/token"), HttpMethod.POST, new HttpEntity<>(body, authHeaders), Map.class);
		} catch (RestClientException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String authorizationToken = null;
				
		if(authResponse != null && authResponse.getBody() != null) {
			authorizationToken = (String) authResponse.getBody().get("authorizationToken");
		}
		return authorizationToken;
	}

	private HttpHeaders getBasicAuthHeaders(String vendor) {
		HttpHeaders authHeaders = new HttpHeaders();
		authHeaders.add("X-AUTH-VENDOR", vendor);
		authHeaders.add("Accept", "application/vnd.developer.att.com.v1+json");
		return authHeaders;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public List<String> fetchUserPermissions(String portalHost, String authorizationToken, String vendor, String login) {
		
		HttpHeaders authHeaders = getBasicAuthHeaders(vendor);
		authHeaders.add("AUTHORIZATION", authorizationToken);
		
		ResponseEntity<Map> principalResponse = null;
		
		try {
			principalResponse = restTemplate.exchange(new URI("https://" + portalHost + "/developer/rest/user/principal/" + login), HttpMethod.GET, new HttpEntity<>(null, authHeaders), Map.class);
		} catch (RestClientException | URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		List<String> principalColl = new ArrayList<String>();
		
		if(principalResponse != null && principalResponse.getBody() != null) {
			principalColl = (List<String>) principalResponse.getBody().get("authorities");
		}
		return principalColl;
	}

}
