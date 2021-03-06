package com.att.developer.service;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.att.developer.bean.Organization;
import com.att.developer.bean.User;
import com.att.developer.bean.api.ApiBundle;

public interface ApiBundleService {

	/**
	 * Retrieves a single Bundle.
	 * <p>
	 * Access-control will be evaluated after this method is invoked.
	 * returnObject refers to the returned object.
	 */
	@PostAuthorize("hasRole('ROLE_SYS_ADMIN') or hasPermission(returnObject, 'WRITE')")
	ApiBundle getApiBundle(String id);

	/**
	 * Retrieves all Bundles .
	 * <p>
	 * Access-control will be evaluated after this method is invoked.
	 * filterObject refers to the returned object list.
	 */
	// Note: hasRole is FIRST in the order as it is less expensive check than
	// hasPermission
	@PostFilter("hasRole('ROLE_SYS_ADMIN') or hasPermission(filterObject, 'READ')")
	List<ApiBundle> getAll();

	/**
	 * Need to be admin to create 
	 */
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	 ApiBundle create(ApiBundle apiBundle, User user);

	/**
	 * Edits a Bundle.
	 * <p>
	 * Access-control will be evaluated before this method is invoked.
	 * <b>#post</b> refers to the current object in the method argument.
	 */
	//TODO: remove hasPermission and replace with hasRole.  Left in place temporarily for demo/poc purposes.
	@PreAuthorize("hasPermission(#apiBundle, 'WRITE')")
	 ApiBundle edit(ApiBundle apiBundle);

	/**
	 * Deletes a Bundle.  Must be an Administrator
	 */
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	 void delete(ApiBundle apiBundle);

//	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')  and #oauth2.isUser()")
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	 void grantPermission(ApiBundle apiBundle, Organization org, User actor);

	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	 void removeAllPermissions(ApiBundle apiBundle, Organization org, User actor);
}
