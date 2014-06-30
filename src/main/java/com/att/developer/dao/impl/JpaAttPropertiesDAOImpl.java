package com.att.developer.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.AttProperties;
import com.att.developer.dao.AttPropertiesDAO;
import com.att.developer.exception.DAOException;

@Component
public class JpaAttPropertiesDAOImpl extends JpaDAO<AttProperties> implements AttPropertiesDAO {

	private static Logger logger = Logger.getLogger(JpaAttPropertiesDAOImpl.class);
	public static final String QUERY_ACTIVE_PROP_BY_IK_FK = "from AttProperties as a where a.itemKey  = :itemKey and a.fieldKey = :fieldKey order by a.version desc";
	public static final String QUERY_VERSIONS_FOR_IK_FK = "select version from AttProperties as a where a.itemKey  = :itemKey and a.fieldKey = :fieldKey order by a.version desc";
	public static final String QUERY_ACTIVE_PROP_BY_IK_FK_AND_VERSION = "from AttProperties as a where a.itemKey  = :itemKey and a.fieldKey = :fieldKey and a.version = :version";
	
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
			throw new DAOException(e);
		}
		return attProperties;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public List<AttProperties> findAllProp(String itemKey, String fieldKey) {
		Query query = entityManager.createQuery(QUERY_ACTIVE_PROP_BY_IK_FK);
		query.setParameter("itemKey", itemKey);
		query.setParameter("fieldKey", fieldKey);
		
		List<AttProperties> attProperties = null;
		
		try {
			attProperties = query.getResultList();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return attProperties;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<String> getVersions(String itemKey, String fieldKey) {
		Query query = entityManager.createQuery(QUERY_VERSIONS_FOR_IK_FK);
		query.setParameter("itemKey", itemKey);
		query.setParameter("fieldKey", fieldKey);
		
		List<String> versionColl = null;
		
		try {
			versionColl = query.getResultList();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return versionColl;
	}

	@Override
	public AttProperties findActivePropByVersion(String itemKey, String fieldKey, String version) {
		Query query = entityManager.createQuery(QUERY_ACTIVE_PROP_BY_IK_FK_AND_VERSION);
		query.setParameter("itemKey", itemKey);
		query.setParameter("fieldKey", fieldKey);
		query.setParameter("version", stringToInt(version));
		
		AttProperties attProperties = null;
		
		try {
			attProperties = (AttProperties) query.getSingleResult();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return attProperties;
	}
	
	public int stringToInt(String param) {
		int value = 0;
        try {
                value = Integer.valueOf(param);
        } catch(NumberFormatException e) {
              // ok to swallow
        }
        return value;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> search(String itemKey) {
		Query query = entityManager.createNamedQuery("AttProperties.getIKWildCard");
		query.setParameter("itemKey", "%" + itemKey + "%");
		List<String> searchResults = null;
		
		try {
			searchResults = query.getResultList();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return searchResults;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> search(String itemKey, String fieldKey) {
		Query query = entityManager.createNamedQuery("AttProperties.getIKFKWildCard");
		query.setParameter("itemKey", itemKey);
		query.setParameter("fieldKey", "%" + fieldKey + "%");
		List<String> searchResults = null;
		
		try {
			searchResults = query.getResultList();
		} catch (NoResultException e) {
			logger.debug(e);
		} catch (Exception e) {
			throw new DAOException(e);
		}
		return searchResults;
	}
}
