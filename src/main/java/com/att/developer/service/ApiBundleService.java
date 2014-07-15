package com.att.developer.service;

import java.util.List;

import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.access.prepost.PreAuthorize;

import com.att.developer.bean.ApiBundle;

public interface ApiBundleService {
	

	 
	 /**
	  *  Retrieves a single Bundle.
	  *  <p>
	  *  Access-control will be evaluated after this method is invoked.
	  *  returnObject refers to the returned object.
	  */
//	 @PostAuthorize("hasPermission(returnObject, 'WRITE') or hasRole('ROLE_USER')")
	@PostAuthorize("hasPermission(returnObject, 'WRITE')")
	 public ApiBundle getSingle(String id);
	 
	 /**
	  *  Retrieves all Bundles	.
	  *  <p>
	  *  Access-control will be evaluated after this method is invoked.
	  *  filterObject refers to the returned object list.
	  */
	 @PostFilter("hasPermission(filterObject, 'READ')")
	 public List<ApiBundle> getAll();
	 
	 /**
	  * Adds a new post.
	  * <p>
	  * We don't provide any access control here because  
	  * the new object doesn't have an id yet. 
	  * <p>
	  * Instead we place the access control on the URL-level because
	  * the Add page shouldn't be visible in the first place.
	  * <p>
	  * There are two places where we can place this restriction:
	  * <pre>
	  * 1. At the controller method
	  * 2. At the external spring-security.xml file</pre>
	  * <p>
	  * 
	  */
	 public ApiBundle add(ApiBundle apiBundle);
	 
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
	 

}
