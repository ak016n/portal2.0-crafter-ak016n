package com.att.developer.dao.impl;

import org.springframework.stereotype.Component;

import com.att.developer.bean.User;
import com.att.developer.dao.UserDAO;

@Component
public class JpaUserDAOImpl extends JpaDAO<User> implements UserDAO {

	public JpaUserDAOImpl() {
		super(User.class);
	}

}
