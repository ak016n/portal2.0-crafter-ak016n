package com.att.developer.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.dao.ApiWrapperDAO;

@Component
public class JpaApiWrapperDAOImpl extends JpaDAO<ApiWrapper> implements ApiWrapperDAO {

    private static final String QUERY_ALL_WRAPPERS = "from ApiWrapper as a";
    private static final String QUERY_WRAPPER_EXISTS = "select a.id from ApiWrapper as a where a.id = :id";

    private final Logger logger = LogManager.getLogger();

    public JpaApiWrapperDAOImpl() {
        super(ApiWrapper.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApiWrapper> getAll() {
        Query query = entityManager.createQuery(QUERY_ALL_WRAPPERS);
        List<ApiWrapper> allBundles = null;
        try {
            allBundles = query.getResultList();
        } catch (NoResultException e) {
            // ok to swallow
            logger.debug("found no ApiWrappers at ALL!!");
        }
        return allBundles;
    }
    
    @Override
    public boolean isExists(String id) {
        Query query = entityManager.createQuery(QUERY_WRAPPER_EXISTS).setParameter("id", id);
        
        String result = null;
        try {
        	result = (String) query.getSingleResult();
        } catch (NoResultException e) {
            // ok to swallow
            logger.debug("no ApiWrappers found!!");
        }
        return StringUtils.isEmpty(result);
    }
}
