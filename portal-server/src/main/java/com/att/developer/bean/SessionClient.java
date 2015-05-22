package com.att.developer.bean;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.provider.ClientDetails;

import com.att.developer.config.SecurityContext;

public class SessionClient {

	private String clientId;
	private String clientName;
	private Collection<? extends GrantedAuthority> authorities;

	public SessionClient(ClientDetails clientDetails) {
		this.setClientId(clientDetails.getClientId());
		this.setAuthorities(clientDetails.getAuthorities());
		this.setClientName((String) clientDetails.getAdditionalInformation().get(SecurityContext.CLIENT_NAME));
	}
	
	public String getClientId() {
		return clientId;
	}

	public void setClientId(String clientId) {
		this.clientId = clientId;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

}
