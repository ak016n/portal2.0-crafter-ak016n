package com.att.developer.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.Organization;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.config.IntegrationContext;
import com.att.developer.dao.OrganizationDAO;
import com.att.developer.dao.UserDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaUserDAOImplTest {

    @Resource
    private OrganizationDAO organizationDAO;
	
    @Resource
    private UserDAO userDAO;
	
	@Test
	public void testCRUD() {
		// create
		User user = new UserBuilder().build();
		userDAO.create(user);
		
		// read
		User afterCreateUser = new UserBuilder().withVanillaUser().withId(user.getId()).build();
		afterCreateUser = userDAO.load(afterCreateUser);
		assertThat(afterCreateUser.getLogin(), CoreMatchers.equalTo(user.getLogin()));
		assertThat(afterCreateUser.getPassword(), CoreMatchers.equalTo(user.getPassword()));
		Assert.assertNotNull(afterCreateUser.getUserStates());
		
		// update
		afterCreateUser.setPassword("cooper");
		User afterUpdate = userDAO.update(afterCreateUser);
		assertThat(afterUpdate.getPassword(), CoreMatchers.equalTo(afterCreateUser.getPassword()));
		
		// delete
		userDAO.delete(afterUpdate);
		assertThat(userDAO.load(afterCreateUser), CoreMatchers.nullValue());
	}
	
	@Test
	public void testUser_withOrganization() {
		// create user
		User user = new UserBuilder().build();
		userDAO.create(user);
		
		// create organization 1
		Organization organization = new OrganizationBuilder().build();
		organization.addUser(user);
		organizationDAO.create(organization);
		
		// create organization 2
		Organization organization2 = new OrganizationBuilder().withName("Friends").withDescription("comedy 1990's").build();
		organization2.addUser(user);
		organizationDAO.create(organization2);
		
		// read
		User afterCreateUser = new UserBuilder().withVanillaUser().withId(user.getId()).build();
		afterCreateUser = userDAO.load(afterCreateUser);
		Assert.assertNotNull(afterCreateUser.getOrganizations());
		Assert.assertEquals(user.getOrganizations().get(0), organization);
		Assert.assertEquals(user.getOrganizations().get(1), organization2);
	}
	
	
}
