package com.att.developer.dao.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.bean.builder.ApiBuilder;
import com.att.developer.bean.builder.ApiWrapperBuilder;
import com.att.developer.config.IntegrationContext;
import com.att.developer.dao.ApiWrapperDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaApiWrapperDAOImplTest {

    @Resource
    private ApiWrapperDAO apiWrapperDAO;
	
	@Test
	public void testCRUD() throws Exception{
		// create
		ApiWrapper apiWrapper = new ApiWrapperBuilder().build();
		apiWrapperDAO.create(apiWrapper);
		
		// read
		ApiWrapper afterCreateApiWrapper = new ApiWrapper();
		afterCreateApiWrapper.setId(apiWrapper.getId());
		afterCreateApiWrapper = apiWrapperDAO.load(afterCreateApiWrapper);
		MatcherAssert.assertThat(afterCreateApiWrapper.getId(), CoreMatchers.equalTo(apiWrapper.getId()));
		MatcherAssert.assertThat(afterCreateApiWrapper.getApi(), CoreMatchers.equalTo(apiWrapper.getApi()));
		MatcherAssert.assertThat(afterCreateApiWrapper.getApiBundle(), CoreMatchers.equalTo(apiWrapper.getApiBundle()));
	
		// update
		afterCreateApiWrapper.setApi(new ApiBuilder().withName("MMS").build());
		
		ApiWrapper afterUpdate = apiWrapperDAO.update(afterCreateApiWrapper);
		MatcherAssert.assertThat(afterCreateApiWrapper.getId(), CoreMatchers.equalTo(apiWrapper.getId()));
		MatcherAssert.assertThat(afterCreateApiWrapper.getApi(), CoreMatchers.equalTo(apiWrapper.getApi()));
		
		// delete
		apiWrapperDAO.delete(afterUpdate);
		MatcherAssert.assertThat(apiWrapperDAO.load(afterCreateApiWrapper), CoreMatchers.nullValue());
	}
}