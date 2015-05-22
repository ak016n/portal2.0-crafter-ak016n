package com.att.developer.bean.builder;

import com.att.developer.bean.UserState;
import com.att.developer.typelist.UserStateType;

public class UserStateBuilder {

	private UserState userState = new UserState();

	public UserStateBuilder() {
		userState.setState(UserStateType.BASIC);
	}

	public UserStateBuilder withState(UserStateType UserStateType) {
		userState.setState(UserStateType);
		return this;
	}

	public UserState build() {
		return userState;
	}

}
