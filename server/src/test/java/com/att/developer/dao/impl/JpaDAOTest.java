package com.att.developer.dao.impl;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.att.developer.annotations.ManageLastDateUpdated;

public class JpaDAOTest {

	private JpaDAO<Bean> jpaDAO;
	
	@Mock
	private EntityManager mockEntityManager;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		jpaDAO = new JpaDAO<Bean>(Bean.class);
		jpaDAO.setEntityManager(mockEntityManager);
	}
	
	@Test
	public void testUpdateLastDateUpdated() {
		
		Bean bean = new Bean();
		jpaDAO.update(bean);
		
		Assert.assertNotNull("lastUpdated should have been automatically populated", bean.getLastUpdated());
	}

	private static class Bean {
		@ManageLastDateUpdated
		private Date lastUpdated;
		
		public Date getLastUpdated() {
			return this.lastUpdated;
		}
	}
	
}
