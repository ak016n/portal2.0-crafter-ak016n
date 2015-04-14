package com.att.developer.service;

import com.att.developer.bean.User;

public interface UserService {

	User getUserByLogin(String login);

	User getUserByEmail(String email);

	User getUser(User user);

}
