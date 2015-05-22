package com.att.developer.bean.wrapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;

import com.att.developer.bean.SessionUser;

public class Principal {

	private SessionUser sessionUser;
	
	public Principal(SessionUser sessionUser) {
		this.sessionUser = sessionUser;
	}
	
	public String getLogin() {
		return sessionUser.getUser().getLogin();
	}
	
	public String getEmail() {
		return sessionUser.getUser().getEmail();
	}

	public String getOrganizationId() {
		return sessionUser.getUser().getDefaultOrganization() != null? sessionUser.getUser().getDefaultOrganization().getId() : null;
	}
	
	public String getOrganizationName() {
		return sessionUser.getUser().getDefaultOrganization() != null? sessionUser.getUser().getDefaultOrganization().getName() : null;
	}
	
	public Collection<String> getAuthorities() {
		List<String> authorities = new ArrayList<String>();
		for(GrantedAuthority authority : sessionUser.getAuthorities()) {
			authorities.add(authority.getAuthority());
		}
		return authorities;
	}

}
