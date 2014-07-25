package com.att.developer.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.att.developer.bean.User;
import com.att.developer.dao.UserDAO;
import com.att.developer.service.UserService;

@Component
public class UserServiceImpl implements UserService {

	@Resource
	private UserDAO userDAO;
	
	@Override
	public User getUserByLogin(String login) {
		return userDAO.loadUserByLogin(login);
	}

	@Override
	public User getUserByEmail(String email) {
		return userDAO.loadUserByEmail(email);
	}
	
	@Override
	public User getUser(User user) {
		return userDAO.load(user);
	}
}
