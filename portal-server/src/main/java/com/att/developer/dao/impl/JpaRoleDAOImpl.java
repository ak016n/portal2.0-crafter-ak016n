package com.att.developer.dao.impl;

import org.springframework.stereotype.Component;

import com.att.developer.bean.Role;
import com.att.developer.dao.RoleDAO;

@Component
public class JpaRoleDAOImpl extends JpaDAO<Role> implements RoleDAO {

	public JpaRoleDAOImpl() {
		super(Role.class);
	}

}
