package com.att.developer.dao.impl;

import org.springframework.stereotype.Component;

import com.att.developer.bean.ApiBundle;
import com.att.developer.dao.ApiBundleDAO;

@Component
public class ApiBundleJpaDAOImpl extends JpaDAO<ApiBundle> implements ApiBundleDAO {

	public ApiBundleJpaDAOImpl() {
		super(ApiBundle.class);
	}

}
