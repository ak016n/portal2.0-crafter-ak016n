package com.att.developer.service.impl;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.dao.EventLogDAO;
import com.att.developer.service.EventLogService;

@Component
public class EventLogServiceImpl implements EventLogService {

	@Resource
	private EventLogDAO eventLogDAO;
	
	@Transactional
	public EventLog createLog(EventLog eventLog) {
		return eventLogDAO.create(eventLog);
	}

}
