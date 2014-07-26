package com.att.developer.service;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.User;

public interface ApiBundleService {

	/**
	 * Retrieves a single Bundle.
	 * <p>
	 * Access-control will be evaluated after this method is invoked.
	 * returnObject refers to the returned object.
	 */
	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	public ApiBundle getApiBundle(String id);

	/**
	 * Retrieves all Bundles .
	 * <p>
	 * Access-control will be evaluated after this method is invoked.
	 * filterObject refers to the returned object list.
	 */
	// Note: hasRole is FIRST in the order as it is less expensive check than
	// hasPermission
	@PostFilter("hasRole('ROLE_SYS_ADMIN') or hasPermission(filterObject, 'READ')")
	public List<ApiBundle> getAll();

	/**
	 * <p>
	 * We don't provide any access control here because the new object doesn't
	 * have an id yet.
	 * <p>
	 * Instead we place the access control on the URL-level because the Add page
	 * shouldn't be visible in the first place.
	 * <p>
	 * There are two places where we can place this restriction:
	 * 
	 * <pre>
	 * 1. At the controller method
	 * 2. At the external spring-security.xml file
	 * </pre>
	 * <p>
	 * 
	 */
	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public ApiBundle create(ApiBundle apiBundle, User user);

	/**
	 * Edits a Bundle.
	 * <p>
	 * Access-control will be evaluated before this method is invoked.
	 * <b>#post</b> refers to the current object in the method argument.
	 */
	@PreAuthorize("hasPermission(#apiBundle, 'WRITE')")
	public ApiBundle edit(ApiBundle apiBundle);

	/**
	 * Deletes a Bundle.
	 * <p>
	 * Access-control will be evaluated before this method is invoked.
	 * <b>#post</b> refers to the current object in the method argument.
	 */
	@PreAuthorize("hasPermission(#apiBundle, 'WRITE')")
	public void delete(ApiBundle apiBundle);

	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public void grantPermission(ApiBundle apiBundle, Organization org);

	@PreAuthorize("hasRole('ROLE_SYS_ADMIN')")
	public void removeAllPermissions(ApiBundle apiBundle, Organization org);
}
