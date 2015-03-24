package com.att.developer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.att.developer.bean.AttProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface GlobalScopedParamService {
	public abstract Map<String, Object> getPropertiesMapFromText(String propertiesText) throws JsonParseException, JsonMappingException, IOException;

	/**
	 * Get global scoped parameter values, first tries environment specific option and then tries global default	
	 * @param key
	 * @return
	 */
	public abstract String get(String key);
	
	/**
	 * Same as get(String key) except, it takes an additional default value in case key is not found
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	public abstract String get(String key, String defaultValue);

	public abstract List<String> getList(String itemKey, String fieldKey, String key);

	public abstract Map<String, String> getMap(String itemKey, String fieldKey, String key);

	/**
	 * High level method returns map of ItemKey and fieldKey
	 * To access more specific keys use getMap(ik, fk, KEY)  
	 */
	public abstract Map<String, Object> getMap(String itemKey, String fieldKey);

	public abstract void reset(String itemKey, String fieldKey);
	
	public abstract AttProperties getProperties(String itemKey, String fieldKey);
	
	public abstract AttProperties getProperties(String itemKey, String fieldKey, String version);
	
	public abstract AttProperties createProperties(AttProperties attProperties, String actor);
	
	public abstract AttProperties updateProperties(AttProperties attProperties);
	
	public abstract AttProperties deleteProperties(AttProperties attProperties);
	
	public abstract List<String> getVersions(String itemKey, String fieldKey);

	public abstract List<String> search(String itemKey);

	public abstract List<String> search(String itemKey, String fieldKey);
}
