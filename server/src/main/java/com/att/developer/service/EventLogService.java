package com.att.developer.service;

import com.att.developer.bean.EventLog;

public interface EventLogService {
	public EventLog createLog(EventLog eventLog);
	
//	@PreAuthorize("#oauth2.isUser() and hasRole('FAKE')")
	//@PreAuthorize("#oauth2.hasRole('ROLE_SYS_ADMIN')")
	public EventLog retrieve(String id);
}
