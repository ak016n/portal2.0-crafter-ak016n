package com.att.developer.bean;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class SessionClient {

	private String clientId;
	private String clientName;
	private Collection<? extends GrantedAuthority> authorities;

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

	public void setAuthorities(
			Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}

}
