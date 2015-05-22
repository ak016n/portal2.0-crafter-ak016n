package com.att.developer.dao.impl;

import javax.persistence.PersistenceException;

import org.springframework.stereotype.Component;

import com.att.developer.bean.Organization;
import com.att.developer.dao.OrganizationDAO;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;

@Component
public class JpaOrganizationDAOImpl extends JpaDAO<Organization> implements OrganizationDAO {

	public JpaOrganizationDAOImpl() {
		super(Organization.class);
	}

	public Organization create(Organization organization) {
		Organization tempOrganization = null;
		try {
			tempOrganization = super.create(organization);
		} catch (PersistenceException e) {
			if(e.getCause() != null && e.getCause().getCause() != null 
					&& e.getCause().getCause() instanceof java.sql.SQLIntegrityConstraintViolationException//com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException
					&& e.getCause().getCause().getMessage().contains("ORA-00001: unique constraint")) {
				throw new DuplicateDataException("Organization name already exists.");
			} else {
				throw new DAOException(e);
			}
		}
		return tempOrganization;
	}
	
	public Organization update(Organization organization) {
		Organization tempOrganization = null;
		try {
			tempOrganization = super.update(organization);
		} catch (PersistenceException e) {
			if(e.getCause() != null && e.getCause().getCause() != null 
					&& e.getCause().getCause() instanceof com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException) {
				throw new DuplicateDataException("Organization name already exists.");
			} else {
				throw new DAOException(e);
			}
		}
		return tempOrganization;
	}
}
