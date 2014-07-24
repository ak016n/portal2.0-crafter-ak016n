package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by event log
 */
public enum EventType {
	
		/** 
		 * EventTypeName(EventId, isCritical())
		 * 
		 * Critical means it needs to be updated within the same transaction
		 */
		GLOBAL_SCOPED_PARAM_CHANGE(1, false),
		
		API_BUNDLE_PERMISSION_UPDATED(100, false),
		
		ACL_ACCESS_ALLOWED(500, false),
		ACL_ACCESS_DENIED(501, false);
		
	    private int id;
	    private boolean critical;

	    private EventType(int id, boolean critical) {
	        this.id = id;
	        this.critical = critical;
	    }

	    public int getId() {
	        return id;
	    }

	    public boolean isCritical() {
	        return critical;
	    }

	    /**
	     * Code for reverse lookup map of typeList key to value pairs 
	     * e.g. key = USER_INVITED_TO_ORG_BY_ADMIN / value = 100
	     *  
	     * Reason for lookup: It is there to prevent repeated looping through collection to find the ENUM. 
	     * Not a big deal in small typeList but would become a performance hit in case of
	     * country typeList and likes.
	     */
	    private static final Map<Integer, EventType> REVERSE_LOOKUP_MAP = new HashMap<>();

	    static {
	        for (EventType s : EnumSet.allOf(EventType.class)) {
	            REVERSE_LOOKUP_MAP.put(s.getId(), s);
	        }
	    }

	    public static EventType getEnumValue(Integer code) {
	        return REVERSE_LOOKUP_MAP.get(code);
	    }
	    
}
