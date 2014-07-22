package com.att.developer.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.ApiBundle;
import com.att.developer.dao.ApiBundleDAO;

@Component
public class JpaApiBundleDAOImpl extends JpaDAO<ApiBundle> implements ApiBundleDAO {

	private static final String QUERY_ALL_BUNDLES = "from ApiBundle as a";
	
	
	private final Logger logger = Logger.getLogger(JpaApiBundleDAOImpl.class);
	 
	public JpaApiBundleDAOImpl() {
		super(ApiBundle.class);
	}

	
	@Override
	public List<ApiBundle> getAll() {
		Query query = entityManager.createQuery(QUERY_ALL_BUNDLES);
        List<ApiBundle> allBundles = null;
        try {
        	allBundles = query.getResultList();
        } catch (NoResultException e) {
            // ok to swallow
        	logger.debug("found no ApiBundles at ALL!!");
        } catch (Exception e) {
        	logger.error(e);
        }
        return allBundles;
	}
}
