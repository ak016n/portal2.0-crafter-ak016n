package com.att.developer.dao;

import com.att.developer.bean.User;

public interface UserDAO extends GenericDAO<User> {
	public User loadUserByLogin(String login);
	public User loadUserByEmail(String email);
}
