package com.att.developer.typelist;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum OrgRelationshipType {
	FIRST_PARTY(1),
	SECOND_PARTY(2),
	THIRD_PARTY(3);
	
    private int id;

    private OrgRelationshipType(int id) {
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
    private static final Map<Integer, OrgRelationshipType> REVERSE_LOOKUP_MAP = new HashMap<>();

    static {
        for (OrgRelationshipType s : EnumSet.allOf(OrgRelationshipType.class)) {
            REVERSE_LOOKUP_MAP.put(s.getId(), s);
        }
    }

    public static OrgRelationshipType getEnumValue(Integer code) {
        return REVERSE_LOOKUP_MAP.get(code);
    }
}
