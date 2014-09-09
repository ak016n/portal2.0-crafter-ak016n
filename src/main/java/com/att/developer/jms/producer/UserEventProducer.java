package com.att.developer.jms.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;

import com.att.developer.bean.User;
import com.att.developer.bean.wrapper.UserJMSWrapper;
import com.att.developer.typelist.JMSEventType;

@Component
public class UserEventProducer {

	public static final String USER_EVENT_TOPIC_DESTINATION = "user.event.topic";
	
	@Autowired
	private JmsTemplate jmsTemplate;
	
	public void updateUser(User user) {
		UserJMSWrapper userJMSWrapper = new UserJMSWrapper(JMSEventType.UPDATE, user);
		jmsTemplate.convertAndSend(USER_EVENT_TOPIC_DESTINATION, userJMSWrapper);
	}
}
