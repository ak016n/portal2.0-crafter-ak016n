package com.att.developer.dao.impl;

import javax.persistence.NoResultException;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.User;
import com.att.developer.dao.UserDAO;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.NonExistentUserException;

@Component
public class JpaUserDAOImpl extends JpaDAO<User> implements UserDAO {

    public static final String QUERY_USER_BY_EMAIL = "from User as a where a.email  = :email";
    public static final String QUERY_USER_BY_LOGIN = "from User as a where a.login  = :login";
	
    private static Logger logger = Logger.getLogger(JpaUserDAOImpl.class);
    
	public JpaUserDAOImpl() {
		super(User.class);
	}

    @Override
    public User load(User entityBean) {
        User user = null;
        try {
            user = super.load(entityBean);
        } catch (DAOException e) {
            // Swallow, two more attempts with login and email loading
        }

        if (user == null && entityBean != null && StringUtils.isNotBlank(entityBean.getLogin())) {
            user = loadUserByLogin(entityBean.getLogin());
        }

        if (user == null && entityBean != null && StringUtils.isNotBlank(entityBean.getEmail())) {
            user = loadUserByEmail(entityBean.getEmail());
        }

        if (user == null) {
            throw new NonExistentUserException(
                    "User does not exist : "
                            + (StringUtils.isNotBlank(entityBean.getLogin()) ? entityBean.getLogin()
                                    : entityBean.getEmail()));
        }

        return user;
    }
    
    public User loadUserByEmail(String email) {
        Query query = entityManager.createQuery(QUERY_USER_BY_EMAIL);
        query.setParameter("email", email);
        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            // ok to swallow
        } catch (Exception e) {
        	logger.error(e);
        }
        return user;
    }

    public User loadUserByLogin(String login) {
        Query query = entityManager.createQuery(QUERY_USER_BY_LOGIN);
        query.setParameter("login", login.toLowerCase());
        User user = null;
        try {
            user = (User) query.getSingleResult();
        } catch (NoResultException e) {
            // ok to swallow
        } catch (Exception e) {
            logger.error(e);
        }
        return user;
    }
	
}
