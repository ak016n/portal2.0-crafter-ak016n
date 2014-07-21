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
import javax.persistence.PersistenceException;
import javax.transaction.Transactional;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.EventLog;
import com.att.developer.dao.AttPropertiesDAO;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;
import com.att.developer.exception.UnsupportedOperationException;

@Service
public class GlobalScopedParamServiceImpl implements GlobalScopedParamService {
	
	public static final String NO_CHANGE_MSG = "No change detected to be updated.";
	public static final String ALREADY_DELETED_MSG = "Update not allowed on already deleted item.";
	public static final String TRY_AGAIN_LATER_MSG = "Unable to update at this time, please try again.";
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
