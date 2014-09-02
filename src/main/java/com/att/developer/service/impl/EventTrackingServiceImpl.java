package com.att.developer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.jms.producer.EventLogProducer;
import com.att.developer.jms.producer.UserEventProducer;
import com.att.developer.service.EventTrackingService;

@Component
public class EventTrackingServiceImpl implements EventTrackingService {

	@Autowired
	private EventLogProducer eventLogProducer;
	
	@Autowired
	private UserEventProducer userEventProducer;
	
	@Override
	public void globalPropertiesChangeEvent(EventLog eventLog) {
		eventLogProducer.convertAndSendMessage(eventLog);
	}

	@Override
	public void writeEvent(EventLog eventLog){
		eventLogProducer.convertAndSendMessage(eventLog);
	}
	
	@Override
	public void userUpdateEvent(User user) {
		userEventProducer.updateUser(user);
	}
	
    private User getActor() {
        SessionUser sessionUser = (SessionUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User actor = sessionUser.getUser();
        return actor;
    }
}
