package com.att.developer.controller;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.service.portal.one.UserProfileService;
import com.att.developer.util.CookieUtil;

@RestController
@RequestMapping("/cg")
public class ContentGatewayController {
	
	private static final String PORTAL_USER = "PORTAL_USER";

	private static final String PORTAL_LOGIN = "portal_login";

	@Inject
	private GlobalScopedParamService globalScopedParamService;

	@Inject
	private EventTrackingService eventTrackingService;
	
    @Inject
    private CookieUtil cookieUtil;
    
    @Inject
    private RestTemplate restTemplate;
    
    @Inject
    private UserProfileService userProfileService;
	
	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}
	
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
	}

	@RequestMapping(value="/{url}", method = RequestMethod.GET)
	public String getProperty(@PathVariable("url") String url, HttpServletRequest request) throws UnsupportedEncodingException {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(url)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Missing required path arguments.").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		Map<String,String> portalCookieMap = getPortalUserMapFrmCookie(request.getCookies());
		
		try {
			List<String> permissions = userProfileService.getUserPermissions("somas");
			
			StringBuilder acl = new StringBuilder();
			boolean isFirst = true;
			for(String each : permissions) {
				if(!isFirst) {
					acl.append(",");
				}
				acl.append(URLEncoder.encode(each.trim(), "UTF-8"));
				isFirst = false;
			}
			
			ResponseEntity<Map> contentResponse = restTemplate.exchange(new URI("http://141.204.193.142:8080/api/att/content_store/page.json?url=/site/website/sample&contextId=064e97b116c0611a1b7c615ed7f6210a&acl=" + acl.toString()), HttpMethod.GET, new HttpEntity<>(null, null), Map.class);
			System.out.println("Content Response : " + contentResponse);
			
			
		} catch (RestClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println(PORTAL_LOGIN + " = " + portalCookieMap.get(PORTAL_LOGIN));
		// TODO decipher cookie
		// call portal from a service for acl
		// call crafter for data
		

		return null;
	}
	
    private Map<String, String> getPortalUserMapFrmCookie(Cookie[] cookies) {
        String cookieValue = this.cookieUtil.getDecryptedCookieValue(cookies, PORTAL_USER);
        Map<String, String> portalUserMap = new HashMap<>();
        if (cookieValue != null) {
            String[] portalUserArr = cookieValue.split("::");
            portalUserMap.put(PORTAL_USER, getValueByPosition(portalUserArr, 0));
            portalUserMap.put(PORTAL_LOGIN, getValueByPosition(portalUserArr, 2));
        }
        return portalUserMap;
    }
    
    private String getValueByPosition(String[] decryptedCookieArr, int position) {
        String value = null;
        if (decryptedCookieArr.length >= position + 1) {
            value = decryptedCookieArr[position];
        }
        return value;
    }

}
