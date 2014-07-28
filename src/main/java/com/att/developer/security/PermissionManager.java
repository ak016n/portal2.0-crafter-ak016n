package com.att.developer.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;

import com.att.developer.bean.Organization;
import com.att.developer.bean.User;

public interface PermissionManager {

	public void createAcl(Class<?> type, Serializable identifier);

	public void grantPermissions(Class<?> type, String identifier, Organization org, Permission permission);

	public void grantPermissions(Class<?> type, String identifier, User user, Permission permission);

	public void changeOwner(Class<?> type, String identifier, Organization newOwningOrg);

	public void changeOwner(Class<?> type, String identifier, User newOwner);
	
	public void deleteAllPermissionsForObject(Class<?> type, String identifier);

	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Organization ownerAndPermissionHolder, Permission permission);

	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, User ownerAndPermissionHolder, Permission permission);

	public List<AccessControlEntry> getAccessControlEntries(Class<?> type, String identifier);

	public void removeAllPermissionForObjectForOrganization(Class<?> type, String identifier, Organization org);

}