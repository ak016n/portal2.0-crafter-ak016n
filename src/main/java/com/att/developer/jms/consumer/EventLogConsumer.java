package com.att.developer.jms.consumer;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.service.EventLogService;

@Component
public class EventLogConsumer {

	@Inject
	private EventLogService eventLogService;
	
    public void handleMessage(EventLog eventLog) {
    	eventLogService.createLog(eventLog);
    }

}


