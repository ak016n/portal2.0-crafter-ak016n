package com.att.developer.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.att.developer.bean.AttProperties;
import com.att.developer.dao.AttPropertiesDAO;
import com.att.developer.service.GlobalScopedParamService;

@Service
public class GlobalScopedParamServiceImpl implements GlobalScopedParamService {
	
	private static final String MAP_DSL = "MAP:";
	private static final String LIST_DSL = "LIST:";
	private static final String ARRAY_DSL = "ARRAY:";
	private static final String SET_DSL = "SET:";
	private static final String DSL_CLOSE_DBL_SQ_BRCKT_REGEX = "\\]\\]";
	private static final String SPACE_REGEX = "\\s+";
	private static final String DSL_OPN_DBL_SQ_BRCKT_REGEX = "\\[\\[";
	private static final String DSL_OPN_DBL_SQ_BRCKT = "[[";
	private static final String EQUALS = "=";
	private static final String COMMA = ",";
	private static final String KEY_SEPARATOR = "_||_";
	private static final String ENV_SPECIFIC_IK = "ENV";
	private static final String DEFAULT_FK = "DEFAULT";
	private static final String GLOBAL_IK = "GLOBAL";
	private static Logger logger = Logger.getLogger(GlobalScopedParamServiceImpl.class);
	private String environment = (System.getProperty(ENV_SPECIFIC_IK) == null)? "DEV" : System.getProperty(ENV_SPECIFIC_IK);
	
	@Resource
	private AttPropertiesDAO attPropertiesDAO;
	
	private ConcurrentHashMap<String, Map<String, Object>> propertiesMap = new ConcurrentHashMap<>(); 
	
	
	
	public void setAttPropertiesDAO(AttPropertiesDAO attPropertiesDAO) {
		this.attPropertiesDAO = attPropertiesDAO;
	}
	
	/**
	 * This is for base properties stored in
	 * GLOBAL/DEFAULT, ENVIRONMENT/DEV, ../INT, ../QA, ../STAGE, ../PROD
	 * @param key
	 */
	@PostConstruct
	protected void initialize() {
		environment = (System.getProperty(ENV_SPECIFIC_IK) == null)? "DEV" : System.getProperty(ENV_SPECIFIC_IK);
		initializeProperties(GLOBAL_IK, DEFAULT_FK);
		initializeProperties(ENV_SPECIFIC_IK, environment);
	}

	private void initializeProperties(String itemKey, String fieldKey) {
		AttProperties attProperties = attPropertiesDAO.findActiveProp(itemKey, fieldKey);
		if(attProperties == null) {
			logger.info("Missing property configuration for : ItemKey: " + itemKey + " FieldKey: " + fieldKey);
			return;
		}
		Properties map = getPropertiesMapFromText(attProperties.getDescription());
		addOrUpdatePropertiesMap(itemKey, fieldKey, map);
	}
	
	private void addOrUpdatePropertiesMap(String itemKey, String fieldKey, Properties map) {
		Map<String, Object> descMap = new HashMap<>();
		String key = buildKeyFromIKFK(itemKey, fieldKey);
		for (Object eachKey : map.keySet()) {
			String value = (String) map.get(eachKey);
			if (value.contains(DSL_OPN_DBL_SQ_BRCKT)) {
				String dslIdentifierArr[] = value.split(DSL_OPN_DBL_SQ_BRCKT_REGEX);
				String dslIdentifier = dslIdentifierArr[0].replaceAll(SPACE_REGEX, StringUtils.EMPTY); // Remove spaces
				String data = dslIdentifierArr[1].replaceAll(DSL_CLOSE_DBL_SQ_BRCKT_REGEX, StringUtils.EMPTY);
				switch (dslIdentifier.toUpperCase()) {
				case SET_DSL:
					Set<String> tempSet = new HashSet<String>(Arrays.asList(getTrimmedStringArray(data)));
					descMap.put((String) eachKey, tempSet);
					break;
				case ARRAY_DSL:
					descMap.put((String) eachKey, getTrimmedStringArray(data));
					break;
				case LIST_DSL:
					descMap.put((String) eachKey, Arrays.asList(getTrimmedStringArray(data)));
					break;
				case MAP_DSL:
					Map<String, String> innerMap = new HashMap<>();
			        for (String each : getTrimmedStringArray(data)) {
			            Object[] objArray = each.split(EQUALS,2);
			            innerMap.put((String)objArray[0], (String)objArray[1]);
			        }
			        descMap.put((String) eachKey, innerMap);
					break;
				default:
					logger.error("Missing proper DSL: Valid values are SET:[[]], LIST:[[]], ARRAY:[[]], MAP:[[]] value received : key : " + key + " value : " + data);
					break;
				}
			} else {
				// then its probably a regular string
				descMap.put((String)eachKey, value);
			}
			propertiesMap.put(key, descMap);
		}

	}

	private String buildKeyFromIKFK(String itemKey, String fieldKey) {
		String key = itemKey + KEY_SEPARATOR + fieldKey;
		return key;
	}

    public String[] getTrimmedStringArray(String propertyStr) {
        if (StringUtils.isBlank(propertyStr)) {
            return ArrayUtils.EMPTY_STRING_ARRAY;
        }

        String[] trimmedArray = propertyStr.split(COMMA);
        for (int i = 0; i < trimmedArray.length; i++) {
            trimmedArray[i] = StringUtils.trimToEmpty(trimmedArray[i]);
        }
        return trimmedArray;
    }
    
	
	@Override
	public Properties getPropertiesMapFromText(String propertiesText) {
        Properties property = new Properties();
        if(StringUtils.isNotBlank(propertiesText)){
            
            try(ByteArrayInputStream bs = new ByteArrayInputStream(propertiesText.getBytes());) {
                property.load(bs);
            } catch (IOException e) {
            	logger.error("Load properties : ", e);
            }
        }
        return property;
    }
	
	@Override
	public String get(String key) {
		boolean found = false;
		String value = null;
		Map<String, Object> envSpecPropertiesMap = propertiesMap.get(buildKeyFromIKFK(ENV_SPECIFIC_IK, environment));
		if(envSpecPropertiesMap != null && envSpecPropertiesMap.containsKey(key)) {
			found = true;
			value = (String) envSpecPropertiesMap.get(key);
		}
		if (!found) {
			Map<String, Object> globalDefaultPropertiesMap = propertiesMap.get(buildKeyFromIKFK(GLOBAL_IK, DEFAULT_FK));
			value = (String) globalDefaultPropertiesMap.get(key);
		}
		return value;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Set<String> getSet(String itemKey, String fieldKey, String key) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		Map<String, Object> tempMap = propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
		return (tempMap != null)? (Set<String>) tempMap.get(key) : null;
	}
	
	@Override
	public String[] getArray(String itemKey, String fieldKey, String key) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		Map<String, Object> tempMap = propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
		return (tempMap != null)? (String[]) tempMap.get(key) : null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<String> getList(String itemKey, String fieldKey, String key) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		Map<String, Object> tempMap = propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
		return (tempMap != null)? (List<String>) tempMap.get(key) : null;
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String> getMap(String itemKey, String fieldKey, String key) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		Map<String, Object> tempMap = propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
		return (tempMap != null)? (Map<String, String>) tempMap.get(key) : null;
	}
	
	/**
	 * High level method returns map of ItemKey and fieldKey
	 * To access more specific keys use getMap(ik, fk, KEY) 
	 * @see com.att.developer.service.impl.GlobalScopedParamService#getMap(java.lang.String,java.lang.String,java.lang.String) 
	 */
	@Override
	public Map<String, Object> getMap(String itemKey, String fieldKey) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		return propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
	}

	
	public AttProperties getProperties(String itemKey, String fieldKey) {
		return attPropertiesDAO.findActiveProp(itemKey, fieldKey);
	}
	
	@Override
	public void reset(String itemKey, String fieldKey) {
		
		if(StringUtils.equals(itemKey, GLOBAL_IK) || StringUtils.equals(itemKey, ENV_SPECIFIC_IK)) {
			initialize();
		} else {
			propertiesMap.put(buildKeyFromIKFK(itemKey, fieldKey), null);
		}
	}
	
}
