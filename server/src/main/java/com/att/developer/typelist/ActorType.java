package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by event log
 */

public enum ActorType {
    
    DEV_PROGRAM_USER(1),
    CSR(2), 
    EDO(3),
    DCM(4),
    SCHEDULED_PROCESS(5),
    WEB_SERVICE(6),
    SUPPORT(7);
    
    private int id;
    
    private ActorType(int id){
        this.id = id;
    }
    
    
    public int getId() {
        return id;
    }
    
    /**
     * Code for reverse lookup map of typeList key to value pairs
     * e.g. key = CSR / value = 2
     * Reason for lookup: It is there to prevent repeated looping through collection to find the ENUM. Not a big deal
     * in small typeList but would become a performance hit in case of country typeList and likes.
     */
    private static final Map<Integer,ActorType> REVERSE_LOOKUP_MAP = new HashMap<>();

    static {
        for(ActorType s : EnumSet.allOf(ActorType.class)){
             REVERSE_LOOKUP_MAP.put(s.getId(), s);
        }
    }
    
    public static ActorType getEnumValue(Integer code) { 
        return REVERSE_LOOKUP_MAP.get(code); 
   }
}
