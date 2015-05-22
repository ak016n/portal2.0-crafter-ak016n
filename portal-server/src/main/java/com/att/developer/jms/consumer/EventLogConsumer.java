package com.att.developer.jms.consumer;

import javax.inject.Inject;

import com.att.developer.bean.EventLog;
import com.att.developer.service.EventLogService;

public class EventLogConsumer {

	@Inject
	private EventLogService eventLogService;
	
    public void handleMessage(EventLog eventLog) {
    	eventLogService.createLog(eventLog);
    }

}


