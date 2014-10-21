package com.att.developer.service.impl;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.service.ContentService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;

@Component
public class ContentServiceImpl implements ContentService {

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

	@SuppressWarnings("rawtypes")
	public Map getContent(String url, String login) {
    	String contextId = globalScopedParamService.get("crafter_context_id", "064e97b116c0611a1b7c615ed7f6210a");
    	String crafterHost = globalScopedParamService.get("crafter_host", "141.204.193.142:8080");
    	
    	Map contentResponse = null;
		try {
			List<String> permissions = userProfileService.getUserPermissions(login);
			
			StringBuilder acl = new StringBuilder();
			boolean isFirst = true;
			for(String each : permissions) {
				if(!isFirst) {
					acl.append(",");
				}
				acl.append(URLEncoder.encode(each.trim(), "UTF-8"));
				isFirst = false;
			}
			
			ResponseEntity<Map> contentResponseEntity = restTemplate.exchange(new URI("http://" + crafterHost + "/api/att/content_store/page.json?url=/site/website/" + url + "&contextId=" + contextId + "&acl=" + acl.toString()), HttpMethod.GET, new HttpEntity<>(null, null), Map.class);
			
			if(contentResponseEntity != null && contentResponseEntity.getBody() != null) {
				contentResponse = contentResponseEntity.getBody();
			}
			
		} catch (RestClientException | URISyntaxException | UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		} 
		return contentResponse;
    }
	
}
