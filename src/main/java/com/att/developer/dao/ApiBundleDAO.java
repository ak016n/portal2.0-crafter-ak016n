package com.att.developer.dao;

import java.util.List;

import com.att.developer.bean.ApiBundle;

public interface ApiBundleDAO extends GenericDAO<ApiBundle> {
	
	public List<ApiBundle> getAll();
}
