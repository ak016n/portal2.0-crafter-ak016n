package com.att.developer.security;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.core.context.SecurityContextHolder;

import com.att.developer.service.EventTrackingService;

public class EventLogAuditLoggerTest {

	@Test
	public void testLogIfNeeded() {
//		SecurityContextHolder.set
		
		EventLogAuditLogger auditLogger = new EventLogAuditLogger();
		EventTrackingService mockEventTrackingService = Mockito.mock(EventTrackingService.class);
		auditLogger.setEventTrackingService(mockEventTrackingService);
		Acl acl = Mockito.mock(AclImpl.class);
		AccessControlEntryImpl acesSomasRead = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.READ, true, false, false);
		
//		auditLogger.logIfNeeded(true, acesSomasRead);
		
//		auditLogger.logIfNeeded(granted, ace);
		
		
	}

}
