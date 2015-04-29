package com.att.developer.dao;

import java.util.List;

import com.att.developer.bean.api.ApiWrapper;

public interface ApiWrapperDAO extends GenericDAO<ApiWrapper> {
	
	public List<ApiWrapper> getAll();
}
