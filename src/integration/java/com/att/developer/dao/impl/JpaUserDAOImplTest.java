package com.att.developer.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.HashSet;
import java.util.Set;

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
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.config.IntegrationContext;
import com.att.developer.dao.OrganizationDAO;
import com.att.developer.dao.RoleDAO;
import com.att.developer.dao.UserDAO;
import com.att.developer.exception.NonExistentUserException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaUserDAOImplTest {

    @Resource
    private OrganizationDAO organizationDAO;
	
    @Resource
    private UserDAO userDAO;
    
    @Resource
    private RoleDAO roleDAO;
	
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
		try {
			userDAO.load(afterCreateUser);
		} catch (NonExistentUserException e) {
			// ok to swallow - as per expectation
		}
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
	
	@Test
	public void testUser_withRole() {
		Role role = new RoleBuilder().build();
		roleDAO.create(role);
		
		// create user
		User user = new UserBuilder().build();
		Set<Role> roleSet = new HashSet<>();
		roleSet.add(role);
		
		user.setRoles(roleSet);
		userDAO.create(user);
		
		//read
		User afterCreateUser = new UserBuilder().withVanillaUser().withId(user.getId()).build();
		afterCreateUser = userDAO.load(afterCreateUser);
		Assert.assertNotNull(afterCreateUser.getRoles());
		Assert.assertTrue(afterCreateUser.getRoles().contains(role));
		
		// delete
		userDAO.delete(afterCreateUser);
		Role afterCreateRole = new RoleBuilder().withVanilaRole().withId(role.getId()).build();
		afterCreateRole = roleDAO.load(afterCreateRole);
		
		Assert.assertNotNull(afterCreateRole);
	}
}
