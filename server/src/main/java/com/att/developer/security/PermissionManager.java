package com.att.developer.security;

import java.io.Serializable;
import java.util.List;

import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;

import com.att.developer.bean.Organization;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;

 public interface PermissionManager {

	void createAcl(Class<?> type, Serializable identifier);

	void grantPermissions(Class<?> type, String identifier, Organization org, Permission permission);

	void grantPermissions(Class<?> type, String identifier, User user, Permission permission);

	void changeOwner(Class<?> type, String identifier, Organization newOwningOrg);

	void changeOwner(Class<?> type, String identifier, User newOwner);
	
	void deleteAllPermissionsForObject(Class<?> type, String identifier);

	void createAclWithPermissionsAndOwner(Class<?> type, String identifier, Organization ownerAndPermissionHolder, Permission permission);

	void createAclWithPermissionsAndOwner(Class<?> type, String identifier, User ownerAndPermissionHolder, Permission permission);

	List<AccessControlEntry> getAccessControlEntries(Class<?> type, String identifier);

	void removeAllPermissionForObjectForOrganization(Class<?> type, String identifier, Organization org);

	void denyPermissions(Class<?> type, String identifier, User user, Permission permission);

	void createAclWithDenyPermissionsAndOwner(Class<?> type, String identifier, User ownerAndPermissionHolder, Permission permission);

	void createAclWithDenyPermissionsAndOwner(Class<?> type, String identifier, SessionUser ownerAndPermissionHolder, Permission permission);

	void createAclWithDenyPermissionsAndOwner(Class<?> type, String identifier, Sid grantedAuthoritiesSid, Permission permission);

}