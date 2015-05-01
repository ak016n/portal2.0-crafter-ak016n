package com.att.developer.bean;

import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

public class PermissionModel {

	private Permission permission;
	private boolean grant;
	private ObjectIdentity parent;
	// principal or granted authority
	private Sid sid;

	public Permission getPermission() {
		return permission;
	}

	public void setPermission(Permission permission) {
		this.permission = permission;
	}

	public boolean isGrant() {
		return grant;
	}

	public void setGrant(boolean grant) {
		this.grant = grant;
	}

	public ObjectIdentity getParent() {
		return parent;
	}

	public void setParent(ObjectIdentity parent) {
		this.parent = parent;
	}

	public Sid getSid() {
		return sid;
	}

	public void setSid(Sid sid) {
		this.sid = sid;
	}

}
