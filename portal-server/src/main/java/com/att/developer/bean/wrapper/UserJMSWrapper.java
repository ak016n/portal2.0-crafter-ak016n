package com.att.developer.bean.wrapper;

import java.io.Serializable;

import com.att.developer.bean.User;
import com.att.developer.typelist.JMSEventType;

public class UserJMSWrapper implements Serializable {

	private static final long serialVersionUID = 4875194592173430781L;
	
	private JMSEventType jmsEventType;
	private User user;

	public UserJMSWrapper(JMSEventType jmsEventType, User user) {
		this.jmsEventType = jmsEventType;
		this.user = user;
	}

	public JMSEventType getJmsEventType() {
		return jmsEventType;
	}

	public void setJmsEventType(JMSEventType jmsEventType) {
		this.jmsEventType = jmsEventType;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
