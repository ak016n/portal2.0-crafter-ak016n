package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum UserStateType {
	PENDING(1), // Waiting for user verification
	BASIC(2),
	BASIC_WCI(3), //Basic with contact info
	INACTIVE(4); 
	
    private int id;

    private UserStateType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    /**
     * Code for reverse lookup map of typeList key to value pairs 
     * e.g. key = USER_INVITED_TO_ORG_BY_ADMIN / value = 100
     *  
     * Reason for lookup: It is there to prevent repeated looping through collection to find the ENUM. 
     * Not a big deal in small typeList but would become a performance hit in case of
     * country typeList and likes.
     */
    private static final Map<Integer, UserStateType> REVERSE_LOOKUP_MAP = new HashMap<>();

    static {
        for (UserStateType s : EnumSet.allOf(UserStateType.class)) {
            REVERSE_LOOKUP_MAP.put(s.getId(), s);
        }
    }

    public static UserStateType getEnumValue(Integer code) {
        return REVERSE_LOOKUP_MAP.get(code);
    }
}
