package com.att.developer.service.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.EventLog;
import com.att.developer.dao.AttPropertiesDAO;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class GlobalScopedParamServiceImpl implements GlobalScopedParamService {
	
	public static final String NO_CHANGE_MSG = "No change detected to be updated.";
	public static final String ALREADY_DELETED_MSG = "Update not allowed on already deleted item.";
	public static final String TRY_AGAIN_LATER_MSG = "Unable to update at this time, please try again.";
	private static final String COMMA = ",";
	private static final String KEY_SEPARATOR = "_||_";
	private static final String ENV_SPECIFIC_IK = "ENV";
	private static final String DEFAULT_FK = "DEFAULT";
	private static final String GLOBAL_IK = "GLOBAL";
	
	private final Logger logger = LogManager.getLogger();
	
	private String environment = (System.getProperty(ENV_SPECIFIC_IK) == null)? "DEV" : System.getProperty(ENV_SPECIFIC_IK);
	
	@Resource
	private AttPropertiesDAO attPropertiesDAO;
	
	@Autowired
	private EventTrackingService eventTrackingService;
	
	private ConcurrentHashMap<String, Map<String, Object>> propertiesMap = new ConcurrentHashMap<>(); 
	
	public void setAttPropertiesDAO(AttPropertiesDAO attPropertiesDAO) {
		this.attPropertiesDAO = attPropertiesDAO;
	}
	
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
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
		Map<String, Object> map = getPropertiesMapFromText(attProperties.getDescription());
		addOrUpdatePropertiesMap(itemKey, fieldKey, map);
	}
	
	private void addOrUpdatePropertiesMap(String itemKey, String fieldKey, Map<String, Object> map) {
		String key = buildKeyFromIKFK(itemKey, fieldKey);
		propertiesMap.put(key, map);
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
	public Map<String, Object> getPropertiesMapFromText(String propertiesText) {
		
		String jsonText = appendBraces(propertiesText);
		JsonFactory factory = new JsonFactory(); 
	    ObjectMapper mapper = new ObjectMapper(factory); 
	    TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};

	    Map<String, Object> mapOfProperties = null;
		try {
			mapOfProperties = mapper.readValue(jsonText, typeRef);
		} catch (IOException e) {
			logger.error("Error while reading property: " + jsonText , e);
			//TODO debate if it is ok to swallow, it shouldn't throw exception at this point in code
		}
        return mapOfProperties;
    }
	
	private String appendBraces(String propertiesText) {
		StringBuilder jsonText = new StringBuilder();
		if (!propertiesText.trim().startsWith("{")) {
			jsonText.append("{");
		}
		
		jsonText.append(propertiesText);
		
		if (!propertiesText.trim().endsWith("}")) {
			jsonText.append("}");
		}
		return jsonText.toString();
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
			value = (globalDefaultPropertiesMap != null) ? (String) globalDefaultPropertiesMap.get(key) : null;
		}
		
		return value;
	}
	
	// Same as above get, additionally it uses defaultValue if primary value not found
	@Override
	public String get(String key, String defaultValue) {
		String value = get(key);
		return StringUtils.isBlank(value)? defaultValue : value;
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
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getMapOfString(String itemKey, String fieldKey, String key) {
		if(propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey)) == null) {
			initializeProperties(itemKey, fieldKey);
		}
		Map<String, Object> tempMap = propertiesMap.get(buildKeyFromIKFK(itemKey, fieldKey));
		return (tempMap != null)? (Map<String, String>) tempMap.get(key) : null;
	}
	
	@Override
	public Map<String, String> getMap(String itemKey, String fieldKey, String key) {
		return getMapOfString(itemKey, fieldKey, key);
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
	
	public List<String> getVersions(String itemKey, String fieldKey) {
		return attPropertiesDAO.getVersions(itemKey, fieldKey);
	}
	
	@Transactional
	public AttProperties createProperties(AttProperties attProperties, String actor) {
		AttProperties lclAttProperties = null;
		try {
			lclAttProperties = attPropertiesDAO.create(attProperties);
			createEvent(attProperties, actor);
		} catch (PersistenceException e) {
			if(e.getCause() != null && e.getCause().getCause() != null 
					&& e.getCause().getCause() instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
				throw new DuplicateDataException("Unique constraint voilated");
			} else {
				throw new DAOException(e);
			}
		}
		return lclAttProperties;
	}

	private void createEvent(AttProperties attProperties, String actor) {
		EventLog eventLog = new EventLog(actor, null, null, EventType.GLOBAL_SCOPED_PARAM_CHANGE, attProperties.toString(), ActorType.DEV_PROGRAM_USER, null);
		eventTrackingService.globalPropertiesChangeEvent(eventLog);
	}
	
	@Transactional
	public AttProperties updateProperties(AttProperties attProperties) {
		AttProperties lclAttProperties = attPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey());
		
		validate(attProperties, lclAttProperties);
		
		AttProperties createAttProp = new AttProperties(attProperties.getItemKey(), attProperties.getFieldKey(), attProperties.getDescription(), lclAttProperties.getVersion() + 1);
		return attPropertiesDAO.create(createAttProp);
	}

	private void validate(AttProperties attProperties, AttProperties lclAttProperties) {
		if(lclAttProperties == null) {
			throw new DAOException(TRY_AGAIN_LATER_MSG);
		}
			
		if(lclAttProperties.isDeleted()) {
			throw new UnsupportedOperationException(ALREADY_DELETED_MSG);
		}
		
		if(StringUtils.equals(lclAttProperties.getDescription(), attProperties.getDescription())) {
			throw new UnsupportedOperationException(NO_CHANGE_MSG);
		}
	}
	
	@Override
	public void reset(String itemKey, String fieldKey) {
		
		if(StringUtils.equals(itemKey, GLOBAL_IK) || StringUtils.equals(itemKey, ENV_SPECIFIC_IK)) {
			initialize();
		} else {
			propertiesMap.put(buildKeyFromIKFK(itemKey, fieldKey), null);
		}
	}

	@Transactional
	public AttProperties deleteProperties(AttProperties attProperties) {
		attProperties.setDeleted(true);
		return attPropertiesDAO.update(attProperties);
	}

	@Override
	public AttProperties getProperties(String itemKey, String fieldKey, String version) {
		return attPropertiesDAO.findActivePropByVersion(itemKey, fieldKey, version);
	}

	@Override
	public List<String> search(String itemKey) {
		return attPropertiesDAO.search(itemKey);
	}
	
	
	@Override
	public List<String> search(String itemKey,String fieldKey) {
		return attPropertiesDAO.search(itemKey, fieldKey);
	}
}
