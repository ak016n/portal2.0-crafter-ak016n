package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OrganizationStateType {
	BASIC(1),
	PLAYGROUND(2),
	FULL_ACCESS(3),
	ENTERPRISE(4);
	
    private int id;

    private OrganizationStateType(int id) {
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
    private static final Map<Integer, OrganizationStateType> REVERSE_LOOKUP_MAP = new HashMap<>();

    static {
        for (OrganizationStateType s : EnumSet.allOf(OrganizationStateType.class)) {
            REVERSE_LOOKUP_MAP.put(s.getId(), s);
        }
    }

    public static OrganizationStateType getEnumValue(Integer code) {
        return REVERSE_LOOKUP_MAP.get(code);
    }
}
