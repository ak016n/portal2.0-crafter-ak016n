package com.att.developer.dao.impl;

import static org.hamcrest.MatcherAssert.assertThat;

import java.util.UUID;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.ApiBundle;
import com.att.developer.config.IntegrationContext;
import com.att.developer.dao.ApiBundleDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaApiBundleDAOImplTest {

    @Resource
    private ApiBundleDAO apiBundleDAO;
	
	@Test
	public void testCRUD() {
		// create
		String userId = UUID.randomUUID().toString();
		ApiBundle apiBundle = new ApiBundle(userId);
		apiBundle.setName("b " + userId);
		apiBundle.setComments("something nice");
		apiBundleDAO.create(apiBundle);
		
		// read
		ApiBundle afterCreateApiBundle = new ApiBundle(userId);
		afterCreateApiBundle = apiBundleDAO.load(afterCreateApiBundle);
		assertThat(afterCreateApiBundle.getCreatedOn(), CoreMatchers.equalTo(apiBundle.getCreatedOn()));
		assertThat(afterCreateApiBundle.getName(), CoreMatchers.equalTo(apiBundle.getName()));
		
		// update
		afterCreateApiBundle.setName("xb " + userId);
		ApiBundle afterUpdate = apiBundleDAO.update(afterCreateApiBundle);
		assertThat(afterUpdate.getName(), CoreMatchers.equalTo(afterCreateApiBundle.getName()));
		
		// delete
		apiBundleDAO.delete(afterUpdate);
		assertThat(apiBundleDAO.load(afterCreateApiBundle), CoreMatchers.nullValue());
	}
	
}
