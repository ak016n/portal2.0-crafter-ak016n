package com.att.developer.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.builder.AttPropertiesBuilder;
import com.att.developer.dao.AttPropertiesDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:applicationContext.xml"})
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaAttPropertiesDAOImplTest {

    @Resource
    private AttPropertiesDAO attPropertiesDAO;
	
	@Test
	public void testCRUD() {
		// create
		AttProperties attProperties = new AttPropertiesBuilder().build();
		attPropertiesDAO.create(attProperties);
		
		// read
		AttProperties afterCreate = new AttPropertiesBuilder().withNonDefault().withId(attProperties.getId()).build();
		afterCreate = attPropertiesDAO.load(afterCreate);
		assertThat(afterCreate.getItemKey(), equalTo(attProperties.getItemKey()));
		assertThat(afterCreate.getFieldKey(), equalTo(attProperties.getFieldKey()));
		
		// update
		afterCreate.setDescription("status=normal");
		AttProperties afterUpdate = attPropertiesDAO.update(afterCreate);
		assertThat(afterUpdate.getDescription(), equalTo(afterCreate.getDescription()));
		
		// delete
		attPropertiesDAO.delete(afterUpdate);
		assertThat(attPropertiesDAO.load(afterCreate), nullValue());
	}
	
	@Test
	public void testFindActiveByIKFK() {
		// create
		AttProperties attProperties = new AttPropertiesBuilder().build();
		attPropertiesDAO.create(attProperties);
		
		// create with same with different version and description
		AttProperties attProperties2 = new AttPropertiesBuilder().withDescription("status=funny").withVersion(2).build();
		attPropertiesDAO.create(attProperties2);
		
		AttProperties findProperties = attPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey());
		
		assertThat(findProperties, is(equalTo(attProperties2)));
	}
	
	@Test
	public void testFindActiveByIKFK_nonExistent() {
		AttProperties findProperties = attPropertiesDAO.findActiveProp("X", "Y");
		
		assertThat(findProperties, is(nullValue()));
	}
	
}
