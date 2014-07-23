package com.att.developer.service;

import com.att.developer.bean.EventLog;

public interface EventTrackingService {

	public void globalPropertiesChangeEvent(EventLog eventLog);

	public void writeEvent(EventLog eventLog);
}
