package com.att.developer.dao.impl;

import org.springframework.stereotype.Component;

import com.att.developer.bean.EventLog;
import com.att.developer.dao.EventLogDAO;
import com.att.developer.exception.UnsupportedOperationException;

@Component
public class JpaEventLogDAOImpl extends JpaDAO<EventLog> implements EventLogDAO {

	public JpaEventLogDAOImpl() {
		super(EventLog.class);
	}

	public EventLog update(EventLog eventLog) {
		throw new UnsupportedOperationException("Update operation is not supported on EventLog");
	}
	
	public void delete(EventLog eventLog) {
		throw new UnsupportedOperationException("Delete operation is not supported on EventLog");
	}
}
