package com.att.developer.dao;

import java.util.List;

import com.att.developer.bean.api.ApiWrapper;

public interface ApiWrapperDAO extends GenericDAO<ApiWrapper> {
	
	List<ApiWrapper> getAll();

	boolean isExists(String id);
}
