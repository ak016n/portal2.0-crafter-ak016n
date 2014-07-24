package com.att.developer.bean.builder;

import com.att.developer.bean.Role;

public class RoleBuilder {

	private Role role = new Role();

	public RoleBuilder() {
		role.setName("SPOCK_ROLE");
		role.setDescription("vulcan role");
	}

	public RoleBuilder withVanilaRole() {
		role = new Role();
		return this;
	}
	
	public RoleBuilder withId(String id) {
		role.setId(id);
		return this;
	}
	
	public RoleBuilder withName(String n){
		role.setName(n);
		return this;
	}
	
	public Role build() {
		return role;
	}
}
