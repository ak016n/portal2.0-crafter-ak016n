package com.att.developer.security;

import java.io.Serializable;

import org.springframework.security.acls.model.Permission;

public interface PermissionManager {

	public void createAcl(Class<?> type, Serializable identifier);

	public void grantPermissions(Class<?> type, String identifier, String recipientUsername, Permission permission);

	public void changeOwner(Class<?> type, String identifier, String newOwnerUsername);

	public void deletePermissionsForObject(Class<?> type, String identifier);

	public void createAclWithPermissionsAndOwner(Class<?> type, String identifier, String ownerId, Permission permission, String permissionRecipient);

}