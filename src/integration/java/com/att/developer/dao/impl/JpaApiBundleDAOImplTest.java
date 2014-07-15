package com.att.developer.dao.impl;

import org.hamcrest.MatcherAssert;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

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
	public void testCRUD() throws Exception{
		// create
		String userId = UUID.randomUUID().toString();
		ApiBundle apiBundle = new ApiBundle(userId);
		apiBundle.setName("b " + userId);
		apiBundle.setComments("something nice");
		apiBundle.setStartDate(Instant.now());
		apiBundleDAO.create(apiBundle);
		
		// read
		ApiBundle afterCreateApiBundle = new ApiBundle(userId);
		afterCreateApiBundle = apiBundleDAO.load(afterCreateApiBundle);
		MatcherAssert.assertThat(afterCreateApiBundle.getCreatedOn(), CoreMatchers.equalTo(apiBundle.getCreatedOn()));
		MatcherAssert.assertThat(afterCreateApiBundle.getStartDate(), CoreMatchers.equalTo(apiBundle.getStartDate()));
		MatcherAssert.assertThat(afterCreateApiBundle.getName(), CoreMatchers.equalTo(apiBundle.getName()));
		
		// update
		afterCreateApiBundle.setName("xb " + userId);
		LocalDateTime nowLocal = LocalDateTime.now().plus(1, ChronoUnit.YEARS);
//		afterCreateApiBundle.setStartDate(Instant.now().plus(365*24*60*60*10000, ChronoUnit.MILLIS));
		afterCreateApiBundle.setStartDate(nowLocal.toInstant(ZoneOffset.ofHours(-8)));
		ApiBundle afterUpdate = apiBundleDAO.update(afterCreateApiBundle);
		MatcherAssert.assertThat(afterUpdate.getName(), CoreMatchers.equalTo(afterCreateApiBundle.getName()));
		MatcherAssert.assertThat(afterUpdate.getStartDate(), CoreMatchers.equalTo(afterCreateApiBundle.getStartDate()));
		
		LocalDateTime localTimeAfterUpdate = LocalDateTime.ofInstant(afterUpdate.getStartDate(), ZoneId.of("America/Los_Angeles"));
		
		Assert.assertEquals(2015, localTimeAfterUpdate.get(ChronoField.YEAR));
		
		// delete
		apiBundleDAO.delete(afterUpdate);
		MatcherAssert.assertThat(apiBundleDAO.load(afterCreateApiBundle), CoreMatchers.nullValue());
	}
	
}
