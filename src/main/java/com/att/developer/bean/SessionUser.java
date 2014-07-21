package com.att.developer.bean;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;

public class SessionUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -992872897986116196L;
	
	private User user;
	
	public SessionUser(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, User user) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired,
				accountNonLocked, authorities);
		this.user = user; 
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
