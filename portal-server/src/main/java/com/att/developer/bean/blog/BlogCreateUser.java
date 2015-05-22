package com.att.developer.bean.blog;

import com.att.developer.bean.User;
import com.fasterxml.jackson.annotation.JsonProperty;

public class BlogCreateUser {
	private User user;

	public BlogCreateUser(User user) {
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
