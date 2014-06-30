package com.att.developer.service;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.att.developer.bean.AttProperties;

public interface GlobalScopedParamService {
	public abstract Properties getPropertiesMapFromText(String propertiesText);

	/**
	 * Get global scoped parameter values, first tries environment specific option and then tries global default	
	 * @param key
	 * @return
	 */
	public abstract String get(String key);

	public abstract Set<String> getSet(String itemKey, String fieldKey,
			String key);

	public abstract String[] getArray(String itemKey, String fieldKey,
			String key);

	public abstract List<String> getList(String itemKey, String fieldKey,
			String key);

	public abstract Map<String, String> getMap(String itemKey, String fieldKey,
			String key);

	/**
	 * High level method returns map of ItemKey and fieldKey
	 * To access more specific keys use getMap(ik, fk, KEY)  
	 */
	public abstract Map<String, Object> getMap(String itemKey, String fieldKey);

	public abstract void reset(String itemKey, String fieldKey);
	
	public abstract AttProperties getProperties(String itemKey, String fieldKey);
	
	public abstract AttProperties getProperties(String itemKey, String fieldKey, String version);
	
	public abstract AttProperties createProperties(AttProperties attProperties);
	
	public abstract AttProperties updateProperties(AttProperties attProperties);
	
	public abstract AttProperties deleteProperties(AttProperties attProperties);
	
	public abstract List<String> getVersions(String itemKey, String fieldKey);

	public abstract List<String> search(String itemKey);

	public abstract List<String> search(String itemKey, String fieldKey);
}
