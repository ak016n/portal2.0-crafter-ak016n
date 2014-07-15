package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Used by event log
 */

public enum ActorType {
    DEV_PROGRAM_USER(1),
    WEB_SERVICE(2),
    SUPPORT(3);
    
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
