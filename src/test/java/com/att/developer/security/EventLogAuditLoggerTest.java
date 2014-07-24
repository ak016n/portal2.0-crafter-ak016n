package com.att.developer.security;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.service.EventTrackingService;
import com.att.developer.typelist.EventType;

public class EventLogAuditLoggerTest {

	@Mock
	private Acl acl;
	
	@Mock
	private EventTrackingService mockEventTrackingService;
	
	private AccessControlEntry ace;
	
	@Before
	public void initialize(){
		MockitoAnnotations.initMocks(this);
		User user = new UserBuilder().build();
		SessionUser sessionUser = new SessionUser(user);
		Authentication authRequest = new UsernamePasswordAuthenticationToken(sessionUser, user.getPassword(), AuthorityUtils.createAuthorityList("ROLE_IGNORED"));
		SecurityContextHolder.getContext().setAuthentication(authRequest);

		ace = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.READ, true, false, false);
	}
	
	@Test
	public void testLogIfNeeded_grantedTrue() {

		EventLogAuditLogger auditLogger = new EventLogAuditLogger();
		auditLogger.setEventTrackingService(mockEventTrackingService);
	
		ArgumentCaptor<EventLog> argCaptor = ArgumentCaptor.forClass(EventLog.class);
		auditLogger.logIfNeeded(true, ace);

		Mockito.verify(mockEventTrackingService, Mockito.times(1)).writeEvent(argCaptor.capture());
		
		Assert.assertEquals("event allowed should have been sent to EventTrackingService", EventType.ACL_ACCESS_ALLOWED, argCaptor.getValue().getEventType());

	}
	

	@Test
	public void testLogIfNeeded_grantedFalse() {

		EventLogAuditLogger auditLogger = new EventLogAuditLogger();
		auditLogger.setEventTrackingService(mockEventTrackingService);
	
		ArgumentCaptor<EventLog> argCaptor = ArgumentCaptor.forClass(EventLog.class);
		auditLogger.logIfNeeded(false, ace);

		Mockito.verify(mockEventTrackingService, Mockito.times(1)).writeEvent(argCaptor.capture());
		
		Assert.assertEquals("event denied should have been sent to EventTrackingService", EventType.ACL_ACCESS_DENIED, argCaptor.getValue().getEventType());

	}

	@Test
	public void testLogIfNeededAllDenied(){
		EventLogAuditLogger auditLogger = new EventLogAuditLogger();
		auditLogger.setEventTrackingService(mockEventTrackingService);
	
		ArgumentCaptor<EventLog> argCaptor = ArgumentCaptor.forClass(EventLog.class);
		
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(ace);
		aces.add(new AccessControlEntryImpl(9, acl, new PrincipalSid("differentPerson"), BasePermission.READ, true, false, false));
		
		List<Sid> sids = new ArrayList<>();
		sids.add(new PrincipalSid("somas"));
		
		auditLogger.logIfNeededAllDenied(aces, sids);

		Mockito.verify(mockEventTrackingService, Mockito.times(1)).writeEvent(argCaptor.capture());
		
		Assert.assertEquals("event denied should have been sent to EventTrackingService", EventType.ACL_ACCESS_DENIED, argCaptor.getValue().getEventType());
		Assert.assertTrue("has wrong Sid!", argCaptor.getValue().getInfo().contains("somas"));
		Assert.assertFalse("has wrong Sid!", argCaptor.getValue().getInfo().contains("differentPerson"));
	}
}
