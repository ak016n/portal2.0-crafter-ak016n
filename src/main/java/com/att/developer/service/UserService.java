package com.att.developer.service;

import com.att.developer.bean.User;

public interface UserService {
	
	public User getUserByLogin(String login);
	public User getUserByEmail(String email);

}
