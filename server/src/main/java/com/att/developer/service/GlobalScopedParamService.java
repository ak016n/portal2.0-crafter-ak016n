package com.att.developer.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.att.developer.bean.AttProperties;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;

public interface GlobalScopedParamService {
	Map<String, Object> getPropertiesMapFromText(String propertiesText) throws JsonParseException, JsonMappingException, IOException;

	/**
	 * Get global scoped parameter values, first tries environment specific option and then tries global default	
	 * @param key
	 * @return
	 */
	String get(String key);
	
	/**
	 * Same as get(String key) except, it takes an additional default value in case key is not found
	 * @param key
	 * @param defaultValue
	 * @return
	 */
	String get(String key, String defaultValue);

	List<String> getList(String itemKey, String fieldKey, String key);

	Map<String, String> getMap(String itemKey, String fieldKey, String key);

	/**
	 * High level method returns map of ItemKey and fieldKey
	 * To access more specific keys use getMap(ik, fk, KEY)  
	 */
	Map<String, Object> getMap(String itemKey, String fieldKey);

	void reset(String itemKey, String fieldKey);
	
	AttProperties getProperties(String itemKey, String fieldKey);
	
	AttProperties getProperties(String itemKey, String fieldKey, String version);
	
	AttProperties createProperties(AttProperties attProperties, String actor);
	
	AttProperties updateProperties(AttProperties attProperties);
	
	AttProperties deleteProperties(AttProperties attProperties);
	
	List<String> getVersions(String itemKey, String fieldKey);

	List<String> search(String itemKey);

	List<String> search(String itemKey, String fieldKey);

	String get(String itemKey, String fieldKey, String key);

	String get(String itemKey, String fieldKey, String key, String defaultVal);
}
