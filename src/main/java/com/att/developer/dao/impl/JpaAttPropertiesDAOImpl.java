package com.att.developer.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.AttProperties;
import com.att.developer.dao.AttPropertiesDAO;

@Component
public class JpaAttPropertiesDAOImpl extends JpaDAO<AttProperties> implements AttPropertiesDAO {

	private static Logger logger = Logger.getLogger(JpaAttPropertiesDAOImpl.class);
	public static final String QUERY_ACTIVE_PROP_BY_IK_FK = "from AttProperties as a where a.itemKey  = :itemKey and a.fieldKey = :fieldKey order by a.version desc";
	
	
	public JpaAttPropertiesDAOImpl() {
		super(AttProperties.class);
	}

	@Override
	public AttProperties findActiveProp(String itemKey, String fieldKey) {
		Query query = entityManager.createQuery(QUERY_ACTIVE_PROP_BY_IK_FK);
		query.setParameter("itemKey", itemKey);
		query.setParameter("fieldKey", fieldKey);
		query.setMaxResults(1);
		
		AttProperties attProperties = null;
		
		try {
			attProperties = (AttProperties) query.getSingleResult();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			logger.error(e);
		}
		return attProperties;
	}
	
}
