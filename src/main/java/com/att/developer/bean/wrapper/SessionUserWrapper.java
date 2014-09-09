package com.att.developer.bean.wrapper;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;

public class SessionUserWrapper {

	private SessionUser sessionUser;
	
	public SessionUserWrapper(SessionUser sessionUser) {
		this.sessionUser = sessionUser;
	}
	
	public User getUser() {
		return sessionUser.getUser();
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return sessionUser.getAuthorities();
	}

}
