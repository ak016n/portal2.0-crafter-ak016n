package com.att.developer.security;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.acls.domain.AuditLogger;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Sid;
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
 * We will *always* log, even if ACL Entry says not to.
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
        User actor = getActor();

        StringBuilder aclMessage = new StringBuilder();

        EventType eventType = EventType.ACL_ACCESS_DENIED;
        if (granted) {
            eventType = EventType.ACL_ACCESS_ALLOWED;
            aclMessage.append("***GRANTED access to " + ace.getAcl().getObjectIdentity() + " due to ACE: \n" + ace);
        } else {
            aclMessage.append("***DENIED access to " + ace.getAcl().getObjectIdentity() + "due to ACE: \n" + ace);
        }

        logger.debug(aclMessage.toString());

        eventTrackingService.writeEvent(new EventLog(actor.getId(), actor.getId(), null, eventType, aclMessage.toString(), ActorType.DEV_PROGRAM_USER, null));
    }

    public void logIfNeededAllDenied(List<AccessControlEntry> aces, List<Sid> sids) {
        Assert.notNull(aces, "AccessControlEntry List required");
        User actor = getActor();
        StringBuilder aclMessage = new StringBuilder();

        aclMessage.append("***DENIED access to object:  ");
        boolean foundMatch = false;
        int i = 0;
        for (AccessControlEntry ace : aces) {
            if (i++ == 0) {
                aclMessage.append("\n\n");
                aclMessage.append(ace.getAcl().getObjectIdentity());
                aclMessage.append("\n\n");
            }
            for (Sid sid : sids) {
                if (ace.getSid().equals(sid)) {
                    foundMatch = true;
                    aclMessage.append(" for sid " + sid);
                    aclMessage.append("\ndue to ACE: \n" + ace);
                }
            }
        }

        if (!foundMatch) {
            aclMessage.append("due to NO matching Sids " + sids);
        }

        logger.debug(aclMessage);

        eventTrackingService.writeEvent(new EventLog(actor.getId(), actor.getId(), null, EventType.ACL_ACCESS_DENIED, aclMessage.toString(), ActorType.DEV_PROGRAM_USER, null));

    }

    private User getActor() {
        SessionUser sessionUser = (SessionUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User actor = sessionUser.getUser();
        return actor;
    }

    public void setEventTrackingService(EventTrackingService service) {
        this.eventTrackingService = service;
    }
}
