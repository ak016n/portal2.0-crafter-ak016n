package com.att.developer.dao.impl;

import java.util.List;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.dao.ApiWrapperDAO;

@Component
public class JpaApiWrapperDAOImpl extends JpaDAO<ApiWrapper> implements ApiWrapperDAO {

    private static final String QUERY_ALL_BUNDLES = "from ApiWrapper as a";

    private final Logger logger = LogManager.getLogger();

    public JpaApiWrapperDAOImpl() {
        super(ApiWrapper.class);
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ApiWrapper> getAll() {
        Query query = entityManager.createQuery(QUERY_ALL_BUNDLES);
        List<ApiWrapper> allBundles = null;
        try {
            allBundles = query.getResultList();
        } catch (NoResultException e) {
            // ok to swallow
            logger.debug("found no ApiBundles at ALL!!");
        }
        return allBundles;
    }
}
