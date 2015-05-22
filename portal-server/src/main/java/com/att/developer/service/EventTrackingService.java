package com.att.developer.service;

import com.att.developer.bean.EventLog;
import com.att.developer.bean.User;

public interface EventTrackingService {

	 void globalPropertiesChangeEvent(EventLog eventLog);

	 void writeEvent(EventLog eventLog);
	
	 void userUpdateEvent(User user);
}
