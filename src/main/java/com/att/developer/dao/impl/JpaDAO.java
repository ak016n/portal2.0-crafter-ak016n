package com.att.developer.dao.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.att.developer.dao.GenericDAO;
import com.att.developer.exception.DAOException;

public class JpaDAO<T> implements GenericDAO<T> {

	private static final String EQUALS = " = ";
	private static final String INSTANCE_TO_STRING = "Instance toString = ";
	private static final String METHOD_NAME = "Method Name = ";
	private static final String CLASS_NAME = "Class Name = ";
	private static final String LINE_BREAK = System.getProperty("line.separator");

	Class<T> beanClass;

	private static Logger logger = Logger.getLogger(JpaDAO.class);
	
	@PersistenceContext
	protected EntityManager entityManager;

	public JpaDAO(Class<T> c) {
		beanClass = c;
	}

	public T update(T entityBean) {
		return entityManager.merge(entityBean);
	}

	public T load(T entityBean) {
		logger.info("inside load method");
		Object idValue = findIdValue(entityBean);
		if (idValue == null) {
			throwException(null, entityBean, new DAOException("Missing Id attribute, you might want to override custom behavior in case of using composite keys"));
		}
		return entityManager.find(beanClass, idValue);
	}

	public T create(T entityBean) {
		entityManager.persist(entityBean);
		/**
		 * Should not be an issue as if there are any problem with persistence
		 * we would have got an exception
		 */
		return entityBean;
	}

	public void delete(T entityBean) {
		entityManager.remove(entityBean);
	}

	protected Object findIdValue(T entityBean) {

		Field[] fields = entityBean.getClass().getDeclaredFields();

		for (Field eachField : fields) {
			Object id = eachField.getAnnotation(javax.persistence.Id.class);
			if (id != null) {
				Object idValue = null;
				try {
					eachField.setAccessible(true); // needed to access private
													// field
					idValue = eachField.get(entityBean);
				} catch (Exception e) {
					throwException(null, entityBean, e);
				}
				return idValue;
			}
		}

		Method[] methods = entityBean.getClass().getMethods();

		for (Method eachMethod : methods) {
			Object id = eachMethod.getAnnotation(javax.persistence.Id.class);
			if (id != null) {
				Object idValue = null;
				try {
					idValue = eachMethod.invoke(entityBean);
				} catch (Exception e) {
					throwException(eachMethod, entityBean, e);
				}
				return idValue;
			}
		}

		return null;
	}

	private void throwException(Method method, Object instance, Exception e) {
		throwException(method, instance, null, null, e);
	}

	private void throwException(Method method, Object instance,
			String extraParamName, String extraParamValue, Throwable e) {

		StringBuilder message = new StringBuilder();
		String methodName = (method != null) ? method.getName() : null; // NOPMD

		message.append(CLASS_NAME).append(instance.getClass().getName())
				.append(LINE_BREAK).append(METHOD_NAME).append(methodName)
				.append(LINE_BREAK).append(INSTANCE_TO_STRING)
				.append(instance.toString());

		if (StringUtils.isNotBlank(extraParamName)) {
			message.append(LINE_BREAK).append(extraParamName).append(EQUALS)
					.append(extraParamValue);
		}

		throw new DAOException(message.toString(), e);
	}

	public EntityManager getEntityManager() {
		return this.entityManager;
	}

}
