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
import com.att.developer.exception.DuplicateDataException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaOrganizationDAOImplTest {

    @Resource
    private OrganizationDAO organizationDAO;
    
    @Resource
    private UserDAO userDAO;
	
	@Test
	public void testCRUD() {
		// create
		Organization organization = new OrganizationBuilder().build();
		organizationDAO.create(organization);
		
		// read
		Organization afterCreateOrganization = new OrganizationBuilder().withVanillaOrganization().withId(organization.getId()).build();
		afterCreateOrganization = organizationDAO.load(afterCreateOrganization);
		assertThat(afterCreateOrganization.getName(), CoreMatchers.equalTo(organization.getName()));
		
		// update
		afterCreateOrganization.setDescription("its a funny show");
		Organization afterUpdate = organizationDAO.update(afterCreateOrganization);
		assertThat(afterUpdate.getDescription(), CoreMatchers.equalTo(afterCreateOrganization.getDescription()));
		
		// delete
		organizationDAO.delete(afterUpdate);
		assertThat(organizationDAO.load(afterCreateOrganization), CoreMatchers.nullValue());
	}
	
	@Test(expected=DuplicateDataException.class)
	public void testCreate_withDuplicateName() {
		// create
		Organization organization = new OrganizationBuilder().build();
		organizationDAO.create(organization);
		
		Organization secondOrganization = new OrganizationBuilder().withDescription("not so funny").build();
		organizationDAO.create(secondOrganization);
	}
	
	@Test
	public void testOrganization_withUser() {
		// create user
		User user = new UserBuilder().build();
		userDAO.create(user);
		
		// create organization
		Organization organization = new OrganizationBuilder().build();
		organization.addUser(user);
		organizationDAO.create(organization);
		
		// read
		Organization afterCreateOrganization = new OrganizationBuilder().withVanillaOrganization().withId(organization.getId()).build();
		afterCreateOrganization = organizationDAO.load(afterCreateOrganization);
		assertThat(afterCreateOrganization.getName(), CoreMatchers.equalTo(organization.getName()));
		Assert.assertTrue(afterCreateOrganization.getUsers().contains(user));
	}
	
	
	@Test
	public void testOrganization_withMultipleUsers() {
		// create user
		User user = new UserBuilder().build();
		userDAO.create(user);
		
		// create user
		User user2 = new UserBuilder().withLogin("leonard").build();
		userDAO.create(user2);
		
		// create organization
		Organization organization = new OrganizationBuilder().build();
		organization.addUser(user);
		organization.addUser(user2);
		organizationDAO.create(organization);
		
		// read
		Organization afterCreateOrganization = new OrganizationBuilder().withVanillaOrganization().withId(organization.getId()).build();
		afterCreateOrganization = organizationDAO.load(afterCreateOrganization);
		assertThat(afterCreateOrganization.getName(), CoreMatchers.equalTo(organization.getName()));
		Assert.assertTrue(afterCreateOrganization.getUsers().contains(user));
		Assert.assertTrue(afterCreateOrganization.getUsers().contains(user2));
	}
	
	@Test
	public void testOrganization_withState() {
		// create organization
		Organization organization = new OrganizationBuilder().build();
		organizationDAO.create(organization);
		
		// read
		Organization afterCreateOrganization = new OrganizationBuilder().withVanillaOrganization().withId(organization.getId()).build();
		afterCreateOrganization = organizationDAO.load(afterCreateOrganization);
		
		Assert.assertNotNull(afterCreateOrganization.getOrganizationStates());
	}
}
