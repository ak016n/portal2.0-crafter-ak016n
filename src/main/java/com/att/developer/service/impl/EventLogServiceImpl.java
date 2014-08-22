package com.att.developer.service.impl;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.att.developer.bean.EventLog;
import com.att.developer.dao.EventLogDAO;
import com.att.developer.service.EventLogService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;

@Service
@Transactional
public class EventLogServiceImpl implements EventLogService {

	
	private EventLogDAO eventLogDAO;
	
	
	@Autowired
	public EventLogServiceImpl(EventLogDAO dao){
	    this.eventLogDAO = dao;
	}
	
	
	@Override
	public EventLog createLog(EventLog eventLog) {
		return eventLogDAO.create(eventLog);
	}
	
	@Override
	public EventLog retrieve(String id) {
	    EventLog log  = new EventLog();
	    log.setId(id);
	    log.setInfo("test some info");
	    log.setActorId("test_actor_id");
	    log.setActorType(ActorType.DEV_PROGRAM_USER);
	    log.setEventType(EventType.API_BUNDLE_PERMISSION_UPDATED);
//	    return eventLogDAO.load(log);
	    return log;
	}

}
