package com.att.developer.bean.builder;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.att.developer.bean.User;
import com.att.developer.bean.UserState;
import com.att.developer.typelist.UserStateType;

public class UserBuilder {
		private User user = new User();
		private Set<UserState> userStates = new HashSet<>();

		public UserBuilder() {
			user.setId(java.util.UUID.randomUUID().toString());
			user.setLogin("sheldon");
			user.setPassword("bazzinga");
			user.setEmail("sheldon@att.com");
			userStates.add(new UserStateBuilder().withState(UserStateType.BASIC).build());
			user.setUserStates(userStates);
			user.setLastUpdated(new Date());
		}
		
		/**
		 * For non-default builder
		 */
		public UserBuilder withVanillaUser() {
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

		public UserBuilder withState(UserStateType userStateType) {
			UserState tempUserState = new UserState();
			tempUserState.setState(userStateType);
			userStates.add(tempUserState);
			return this;
		}
		
		public User build() {
			return user;
		}
}
