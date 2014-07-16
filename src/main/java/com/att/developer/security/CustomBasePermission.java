package com.att.developer.security;

import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.Permission;


/**
 * Extension of the BasePermission class to allow for combination of permissions. 
 * 
 * You can either use the READ_WRITE combo or pass in a list of Permission objects to the instanceOf 
 * 
 * @author so1234
 *
 */
public class CustomBasePermission extends BasePermission {

	private static final long serialVersionUID = 5162279896437833577L;
	
	public static final CustomBasePermission READ_WRITE = new CustomBasePermission(3);

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

		//make sure we are not returning existing READ_WRITE. 
		if(mask == READ_WRITE.getMask()){
			return READ_WRITE;
		}
		
		return new CustomBasePermission(mask);
		
	}

}
