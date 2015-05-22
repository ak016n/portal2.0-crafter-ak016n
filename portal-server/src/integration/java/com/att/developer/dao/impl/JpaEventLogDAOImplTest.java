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

import com.att.developer.bean.EventLog;
import com.att.developer.bean.builder.EventLogBuilder;
import com.att.developer.config.IntegrationContext;
import com.att.developer.dao.EventLogDAO;
import com.att.developer.exception.UnsupportedOperationException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes=IntegrationContext.class, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class JpaEventLogDAOImplTest {

    @Resource
    private EventLogDAO eventLogDAO;
	
	@Test
	public void testCreateAndLoad() {
		// create
		EventLog eventLog = new EventLogBuilder().build();
		eventLogDAO.create(eventLog);
		
		// read
		EventLog afterCreateEventLog = new EventLogBuilder().withVanillaEventLog().withId(eventLog.getId()).build();
		afterCreateEventLog = eventLogDAO.load(afterCreateEventLog);
		assertThat(afterCreateEventLog.getActorId(), CoreMatchers.equalTo(eventLog.getActorId()));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUpdate() {
		// create
		EventLog eventLog = new EventLogBuilder().build();
		eventLogDAO.create(eventLog);
		
		eventLog.setActorId("cooper");
		eventLogDAO.update(eventLog);
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testDelete() {
		// create
		EventLog eventLog = new EventLogBuilder().build();
		eventLogDAO.create(eventLog);
		
		eventLogDAO.delete(eventLog);
	}
}
