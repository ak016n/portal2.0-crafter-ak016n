package com.att.developer.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.att.developer.bean.SessionClient;

public class OAuth2AuthSessionClient extends OAuth2Authentication {

	private static final long serialVersionUID = 8827137658173591152L;
	
	private SessionClient sessionClient;
	
	public OAuth2AuthSessionClient(OAuth2Request clientAuthentication, SessionClient sessionClient) {
		super(clientAuthentication, null);
		this.sessionClient = sessionClient;
	}
	
	public OAuth2AuthSessionClient(OAuth2Request clientAuthentication, Authentication userAuthentication) {
		super(clientAuthentication, userAuthentication);
	}

	public Object getPrincipal() {
		return sessionClient;
	}

}
