package com.att.developer.service;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.User;

public interface EventTrackingService {

	public void globalPropertiesChangeEvent(EventLog eventLog);

	public void writeEvent(EventLog eventLog);
	
	public void userUpdateEvent(User user);
}
