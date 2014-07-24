package com.att.developer.bean.builder;

import java.time.Instant;
import java.util.Date;

import com.att.developer.bean.EventLog;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;

public class EventLogBuilder {
		private EventLog eventLog = new EventLog();

		public EventLogBuilder() {
			eventLog.setId(java.util.UUID.randomUUID().toString());
			eventLog.setActorId("Hofstadter");
			eventLog.setActorType(ActorType.DEV_PROGRAM_USER);
			eventLog.setImpactedUserId("kudrapali");
			eventLog.setEventType(EventType.GLOBAL_SCOPED_PARAM_CHANGE);
			eventLog.setInfo("comment");
			eventLog.setOrgId("big bang");
			eventLog.setTransactionId("nimoy");
			eventLog.setCreatedOn(Instant.now());
		}
		
		public EventLogBuilder withVanillaEventLog() {
			eventLog = new EventLog();
			return this;
		}
		
		public EventLogBuilder withId(String id) {
			eventLog.setId(id);
			return this;
		}
		
		public EventLog build() {
			return eventLog;
		}
}
