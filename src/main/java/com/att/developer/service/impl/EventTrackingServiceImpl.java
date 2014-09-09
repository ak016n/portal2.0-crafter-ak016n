package com.att.developer.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.SessionClient;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.jms.producer.EventLogProducer;
import com.att.developer.jms.producer.UserEventProducer;
import com.att.developer.service.EventTrackingService;
import com.att.developer.typelist.ActorType;

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
	
	/**
	 * TODO implement it in builder pattern
	 * @param eventLog
	 */
	private void updateActorAndType(EventLog eventLog) {
		String actor = StringUtils.EMPTY;
		ActorType actorType = null;
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (principal instanceof SessionUser) {
			User user = ((SessionUser) principal).getUser();
			actor = user.getId();
			// User object should have 
			//actorType = ActorType.
		} else if (principal instanceof SessionClient) {
			SessionClient sessionClient = (SessionClient) principal;
			actor = sessionClient.getClientId();
			// TODO try catch for exceptions
			actorType = ActorType.valueOf(sessionClient.getClientName().toUpperCase());
		} else {
			 actor = (String) principal;
		}

		eventLog.setActorId(actor);
		eventLog.setActorType(actorType);
	}
}
