package com.att.developer.jms.producer;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.att.developer.bean.User;

@Component
public class UserEventProducer {

	public static final String USER_EVENT_TOPIC_DESTINATION = "user.event.topic";
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void updateUser(User user) {
		Map<String, User> userMap = new HashMap<>();
		userMap.put("update", user);
		jmsTemplate.convertAndSend(USER_EVENT_TOPIC_DESTINATION, userMap);
	}
}
