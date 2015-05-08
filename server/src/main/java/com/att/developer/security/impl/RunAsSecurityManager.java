package com.att.developer.security.impl;

import javax.inject.Inject;

import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("runAsSecurityManager")
public class RunAsSecurityManager {

	@Inject
	private PermissionEvaluator permissionEvaluator;

	public void setPermissionEvaluator(PermissionEvaluator permissionEvaluator) {
		this.permissionEvaluator = permissionEvaluator;
	}
	
	public Object hasPermission(Authentication authentication, Object filterTarget, String filterExpression) {
		return permissionEvaluator.hasPermission(authentication, filterTarget, filterExpression);
	}


}
