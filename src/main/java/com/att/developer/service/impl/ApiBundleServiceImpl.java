package com.att.developer.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.att.developer.bean.ApiBundle;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.service.ApiBundleService;

@Service
@Transactional
public class ApiBundleServiceImpl implements ApiBundleService {
	
	private static final Logger logger = Logger.getLogger(ApiBundleServiceImpl.class);
	
	@Resource
	private ApiBundleDAO apiBundleDAO;
	
	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	@Override
	public ApiBundle getSingle(String id) {
		return apiBundleDAO.load(new ApiBundle(id));
	}

	@Override
	public List<ApiBundle> getAll() {
		// TODO Implement
		return null;
	}

	@Override
	public ApiBundle add(ApiBundle bean) {
		return apiBundleDAO.create(bean);
	}

	@Override
	public ApiBundle edit(ApiBundle apiBundle) {
		return apiBundleDAO.update(apiBundle);
	}

	@Override
	public void delete(ApiBundle apiBundle) {
		apiBundleDAO.delete(apiBundle);
	}
	


/*

	public AttProperties getProperties(String itemKey, String fieldKey) {
		return attPropertiesDAO.findActiveProp(itemKey, fieldKey);
	}
	
	public List<String> getVersions(String itemKey, String fieldKey) {
		return attPropertiesDAO.getVersions(itemKey, fieldKey);
	}
	
	@Transactional
	public AttProperties createProperties(AttProperties attProperties) {
		AttProperties lclAttProperties = null;
		try {
			lclAttProperties = attPropertiesDAO.create(attProperties);
		} catch (PersistenceException e) {
			if(e.getCause() instanceof org.hibernate.exception.ConstraintViolationException) {
				throw new DuplicateDataException("Unique constraint voilated");
			} else {
				throw new DAOException(e);
			}
		}
		return lclAttProperties;
	}
	
	@Transactional
	public AttProperties updateProperties(AttProperties attProperties) {
		AttProperties lclAttProperties = attPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey());
		
		if(lclAttProperties == null) {
			throw new DAOException("Unable to update at this time, please try again.");
		}
			
		if(lclAttProperties.isDeleted()) {
			throw new DAOException("Update not allowed on already deleted item.");
		}
		
		if(StringUtils.equals(lclAttProperties.getDescription(), attProperties.getDescription())) {
			throw new DAOException("No change detected to be updated.");
		}
		
		AttProperties createAttProp = new AttProperties(attProperties.getItemKey(), attProperties.getFieldKey(), attProperties.getDescription(), lclAttProperties.getVersion() + 1);
		return attPropertiesDAO.create(createAttProp);
	}
	

/*
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
	
*/
}
