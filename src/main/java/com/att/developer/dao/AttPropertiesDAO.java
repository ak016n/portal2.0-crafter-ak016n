package com.att.developer.dao;

import com.att.developer.bean.AttProperties;

public interface AttPropertiesDAO extends GenericDAO<AttProperties> {

	public abstract AttProperties findActiveProp(String itemKey, String fieldKey);
	
}
