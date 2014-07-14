package com.att.developer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.jms.producer.EventLogProducer;
import com.att.developer.service.EventTrackingService;

@Component
public class EventTrackingServiceImpl implements EventTrackingService {

	@Autowired
	private EventLogProducer eventLogProducer;
	
	@Override
	public void globalPropertiesChangeEvent(EventLog eventLog) {
		eventLogProducer.convertAndSendMessage(eventLog);
	}

}
