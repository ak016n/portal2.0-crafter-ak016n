package com.att.developer.dao;

import java.util.List;

import com.att.developer.bean.AttProperties;

public interface AttPropertiesDAO extends GenericDAO<AttProperties> {

	public abstract AttProperties findActiveProp(String itemKey, String fieldKey);

	public abstract List<AttProperties> findAllProp(String itemKey, String fieldKey);

	public abstract List<String> getVersions(String itemKey, String fieldKey);

	public abstract AttProperties findActivePropByVersion(String itemKey, String fieldKey, String version);
	
	public abstract List<String> search(String itemKey);
	
	public abstract List<String> search(String itemKey, String fieldKey);
	
}
