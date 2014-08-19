package com.att.developer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.EventLog;
import com.att.developer.service.EventLogService;
import com.att.developer.service.EventTrackingService;

@RestController
@RequestMapping("/cauth/eventLog")
public class EventLogController {

    private EventTrackingService eventTrackingService;
    private EventLogService eventLogService;
    
    @Autowired
    public EventLogController(EventTrackingService trackingSvc, EventLogService logSvc){
        this.eventTrackingService = trackingSvc;
        this.eventLogService = logSvc;
    }
    
    
    @RequestMapping(method = RequestMethod.POST)
    public EventLog create(@RequestBody EventLog eventLog) {
        eventTrackingService.writeEvent(eventLog);
        
        return eventLog;
    }
    
    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public EventLog retrieve(@PathVariable("id") String id) {
        return eventLogService.retrieve(id);
    }
    

}
