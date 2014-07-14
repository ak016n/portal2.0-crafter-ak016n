package com.att.developer.jms.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;

@Component
public class EventLogProducer {

	private static final String EVENT_QUEUE_DESTINATION = "event.queue";
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void convertAndSendMessage(EventLog eventLog) {
		jmsTemplate.convertAndSend(EVENT_QUEUE_DESTINATION, eventLog);
	}
}
