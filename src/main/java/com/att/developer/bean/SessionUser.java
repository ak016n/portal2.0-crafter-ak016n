package com.att.developer.bean;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;

/**
 * We are using userId which is a generated UUID for the 'username' that Spring
 * requires. This guarantees that if we ever want to change the username, it
 * will not break the underlying Spring security ACL mappings as those mappings
 * will be using
 * 
 * @author som
 *
 */
public class SessionUser extends org.springframework.security.core.userdetails.User {

	private static final long serialVersionUID = -992872897986116196L;

	private User user;

	public SessionUser(String username, String password, boolean enabled, boolean accountNonExpired,
			boolean credentialsNonExpired, boolean accountNonLocked,
			Collection<? extends GrantedAuthority> authorities, User user) {
		super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
		this.user = user;
	}
	
	
	public SessionUser(String username, String password, Collection<? extends GrantedAuthority> authorities, User user) {
		this(username, password, true, true, true, true, authorities, user);
		
	}
 
	/**
	 * builds a User using default rules
	 * <ul>
	 * <li>all booleans set to true in full constructor</li>
	 * <li>username set to user.getId</li>
	 * <li>password set to user.getEncryptedPassword</li>
	 * <li>empty set of Authorities</li>
	 * 
	 * </ul>
	 * 
	 * @param user
	 */
	public SessionUser(User user) {
		this(user.getId(), user.getEncryptedPassword(), Collections.<GrantedAuthority>emptySet(), user);
	}
	

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
