package com.att.developer.security.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.UserState;

public class AuthenticationUtil {
	
	public static Authentication buildOrgAuthentication(Organization organization) {
		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		addOrgAuthority(authorities, organization);
		
		return new CustomAuthentication(organization.getName(), authorities, true);
	}

	public static Authentication buildUserAuthentication(User user) {
		org.springframework.security.core.userdetails.User coreUser = buildSecurityUser(user);
		return new CustomAuthentication(coreUser.getUsername(), coreUser.getAuthorities(), true);
	}
	
	/*
	 * Build Java user principal
	 */
	public static org.springframework.security.core.userdetails.User buildSecurityUser(User portalUser) {
		String userId = portalUser.getId();
		String password = portalUser.getEncryptedPassword();
		// There to see if we have to use any information from actual user
		// object to determine the state of others

		Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

		for (UserState userState : portalUser.getUserStates()) {
			authorities.add(new SimpleGrantedAuthority(userState.getState().name()));
		}

		addRoles(authorities, portalUser.getRoles());

		List<Organization> orgs = portalUser.getOrganizations();

		addOrgAuthority(authorities, orgs);

		// Note: We are using userId which is a generated UUID for the
		// 'username' that Spring requires.
		// TODO hierarchical organization state
		SessionUser user = new SessionUser(userId, password, authorities, portalUser);
		return user;
	}

	private static void addOrgAuthority(Collection<GrantedAuthority> authorities, List<Organization> orgs) {
		if (!CollectionUtils.isEmpty(orgs)) {
			Organization organization = orgs.get(0);
			addOrgAuthority(authorities, organization);
		}
	}

	private static void addOrgAuthority(Collection<GrantedAuthority> authorities, Organization organization) {
		authorities.add(new SimpleGrantedAuthority(organization.getId()));
		authorities.add(new SimpleGrantedAuthority(organization.getOrganizationType().name()));
	}

	private static void addRoles(Collection<GrantedAuthority> authorities, Set<Role> roles) {
		if (roles != null) {
			for (Role role : roles) {
				authorities.add(new SimpleGrantedAuthority(role.getName()));
			}
		}
	}
	
	private static class CustomAuthentication implements Authentication {

		private static final long serialVersionUID = -3010292963872391524L;
		
		String name;
		Collection<? extends GrantedAuthority> authorities;
		boolean authenticated;
		
		public CustomAuthentication(String name, Collection<? extends GrantedAuthority> authorities, boolean authenticated) {
			this.name = name;
			this.authorities = authorities;
			this.authenticated = authenticated;
		}
		
		@Override
		public String getName() {
			return name;
		}

		@Override
		public Collection<? extends GrantedAuthority> getAuthorities() {
			return authorities;
		}
		
		@Override
		public Object getCredentials() {
			return "";
		}

		@Override
		public Object getDetails() {
			return null;
		}

		@Override
		public Object getPrincipal() {
			return name;
		}

		@Override
		public boolean isAuthenticated() {
			return authenticated;
		}

		@Override
		public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
			this.authenticated = isAuthenticated;
		}
		
	}

	public static String getUserSid(User user) {
		String userId = user.getId();
		List<Organization> orgs = user.getOrganizations();

		if (!CollectionUtils.isEmpty(orgs)) {
			userId = userId + "_" + orgs.get(0).getId();
		}
		return userId;
	}
}
