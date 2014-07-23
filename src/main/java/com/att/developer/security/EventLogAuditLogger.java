package com.att.developer.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.AuditableAccessControlEntry;
import org.springframework.util.Assert;


public class EventLogAuditLogger implements AuditLogger {

	
	private final Logger logger = LogManager.getLogger(EventLogAuditLogger.class);

	
	public void logIfNeeded(boolean granted, AccessControlEntry ace) {
		Assert.notNull(ace, "AccessControlEntry required");

		if (ace instanceof AuditableAccessControlEntry) {
			AuditableAccessControlEntry auditableAce = (AuditableAccessControlEntry) ace;

			if (granted && auditableAce.isAuditSuccess()) {
				logger.warn("GRANTED due to ACE: " + ace);
			} else if (!granted && auditableAce.isAuditFailure()) {
				logger.warn("DENIED due to ACE: " + ace);
			}
		}
	}

}