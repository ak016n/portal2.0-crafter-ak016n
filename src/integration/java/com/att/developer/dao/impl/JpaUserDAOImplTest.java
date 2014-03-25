package com.att.developer.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.User;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.config.IntegrationConfig;
import com.att.developer.dao.UserDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationConfig.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaUserDAOImplTest {

    @Resource
    private UserDAO userDAO;
	
	@Test
	public void testCRUD() {
		// create
		User user = new UserBuilder().build();
		userDAO.create(user);
		
		// read
		User afterCreateUser = new UserBuilder().withNonDefault().withId(user.getId()).build();
		afterCreateUser = userDAO.load(afterCreateUser);
		assertThat(afterCreateUser.getLogin(), CoreMatchers.equalTo(user.getLogin()));
		assertThat(afterCreateUser.getPassword(), CoreMatchers.equalTo(user.getPassword()));
		
		// update
		afterCreateUser.setPassword("cooper");
		User afterUpdate = userDAO.update(afterCreateUser);
		assertThat(afterUpdate.getPassword(), CoreMatchers.equalTo(afterCreateUser.getPassword()));
		
		// delete
		userDAO.delete(afterUpdate);
		assertThat(userDAO.load(afterCreateUser), CoreMatchers.nullValue());
	}
	
}
