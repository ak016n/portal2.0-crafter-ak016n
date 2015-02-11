package com.att.developer.bean;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogUser {
	private User user;

	public BlogUser(User user) {
		this.user = user;
	}

	public String getUsername() {
		return user.getLogin();
	}

	public String getName() {
		return user.getLogin();
	}

	@JsonProperty("first_name")
	public String getFirstName() {
		return user.getFirstName();
	}

	@JsonProperty("last_name")
	public String getLastName() {
		return user.getLastName();
	}

	public String getPassword() {
		return "password123";
	}

	public String getEmail() {
		return user.getEmail();
	}

}
