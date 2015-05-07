package com.att.developer.service;

import com.att.developer.bean.Organization;

public interface OrganizationService {

	//@PreAuthorize("hasPermission(#org, 'READ')")
	Organization getOrganization(Organization org);

	Organization createOrganization(Organization organization);

	Organization getOrganization(String orgId);

}