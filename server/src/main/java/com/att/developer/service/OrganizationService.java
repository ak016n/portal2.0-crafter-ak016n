package com.att.developer.service;

import com.att.developer.bean.Organization;

public interface OrganizationService {

	//@PreAuthorize("hasPermission(#org, 'READ')")
	public abstract Organization getOrganization(Organization org);

	public abstract Organization createOrganization(Organization organization);

}