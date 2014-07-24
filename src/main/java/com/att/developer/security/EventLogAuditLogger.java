package com.att.developer.security;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.service.EventTrackingService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;


/**
 * Writes to Log4j and more importantly to our EventLog audit trail. 
 * 
 * Replacement for Spring's default ConsoleAuditLogger.
 *
 *  We will *always* log, even if ACL Entry says not to.
 * 
 * @author so1234
 *
 */
public class EventLogAuditLogger implements AuditLogger {

	
	private final Logger logger = LogManager.getLogger();

	
	@Autowired
	private EventTrackingService eventTrackingService;
	
	
	public void logIfNeeded(boolean granted, AccessControlEntry ace) {
		Assert.notNull(ace, "AccessControlEntry required");
		SessionUser sessionUser = (SessionUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		User actor = sessionUser.getUser();
		
		if (granted) {
			logger.debug("***GRANTED due to ACE: {} for ACL {} ", ace, ace.getAcl());
		} else if (!granted) {
			logger.debug("***DENIED due to ACE: {} for ACL {} ", ace, ace.getAcl());
		}
		
		this.writeEvent(actor, ace, granted);
	}

	private void writeEvent(User actor, AccessControlEntry ace, boolean granted){
		EventType eventType = EventType.ACL_ACCESS_DENIED;
		if(granted){
			eventType = EventType.ACL_ACCESS_ALLOWED;
		}
		
		String aclInfo = "ace is " + ace + " for acl " + ace.getAcl();
		EventLog eventLog = new EventLog(actor.getId(), actor.getId(), null, eventType, aclInfo, ActorType.DEV_PROGRAM_USER, null);
		
		eventTrackingService.writeEvent(eventLog);
	}
	
	public void setEventTrackingService(EventTrackingService service){
		this.eventTrackingService = service;
	}
}