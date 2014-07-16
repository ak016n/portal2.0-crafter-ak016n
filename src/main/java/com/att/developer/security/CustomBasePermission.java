package com.att.developer.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;

public class CustomBasePermission extends BasePermission {

	private static final long serialVersionUID = 5162279896437833577L;
	
	public static final Permission READ_WRITE = new CustomBasePermission(3);

	protected CustomBasePermission(int mask) {
		super(mask);
	}

	protected CustomBasePermission(int mask, char code) {
		super(mask, code);
	}
	
	public static CustomBasePermission instanceOf(Permission... permissions){
		int mask = 0;
		for(Permission permission : permissions){
			mask = mask | permission.getMask();
		}
		
		return new CustomBasePermission(mask);
		
	}

}
