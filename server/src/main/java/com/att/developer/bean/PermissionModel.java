package com.att.developer.bean;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class PermissionModel {

	private Permission permission;
	private boolean grant;
	private ObjectIdentity parent;
	// principal or granted authority
	private String principalSid;
	private String grantedAuthoritySid;
	
	@JsonIgnore
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

	public String getPrincipalSid() {
		return principalSid;
	}

	public void setPrincipalSid(String principalSid) {
		this.principalSid = principalSid;
	}

	public String getGrantedAuthoritySid() {
		return grantedAuthoritySid;
	}

	public void setGrantedAuthoritySid(String grantedAuthoritySid) {
		this.grantedAuthoritySid = grantedAuthoritySid;
	}

	public Sid getSid() {
		if (sid == null) {
			if (StringUtils.isNoneBlank(principalSid)) {
				sid = new PrincipalSid(principalSid);
			} else {
				sid = new GrantedAuthoritySid(grantedAuthoritySid);
			}
		}

		return sid;
	}


}
