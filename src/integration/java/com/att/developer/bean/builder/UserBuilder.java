package com.att.developer.bean.builder;

import java.util.Date;

import com.att.developer.bean.User;

public class UserBuilder {
		private User user = new User();

		public UserBuilder() {
			user.setId(java.util.UUID.randomUUID().toString());
			user.setLogin("sheldon");
			user.setPassword("bazzinga");
			user.setLastUpdated(new Date());
		}
		
		/**
		 * For non-default builder
		 */
		public UserBuilder withNonDefault() {
			user = new User();
			return this;
		}
		
		public UserBuilder withId(String id) {
			user.setId(id);
			return this;
		}

		public UserBuilder withLogin(String login) {
			user.setLogin(login);
			return this;
		}

		public UserBuilder withPassword(String password) {
			user.setPassword(password);
			return this;
		}

		public UserBuilder withLastUpdated(Date lastUpdated) {
			user.setLastUpdated(lastUpdated);
			return this;
		}

		public User build() {
			return user;
		}
}
