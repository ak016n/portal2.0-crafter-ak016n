package com.att.developer.controller;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.Organization;
import com.att.developer.bean.PermissionModel;
import com.att.developer.bean.api.ApiBundle;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.OrganizationService;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Inject
    private PermissionManager permissionManager;
    
    @Inject
    private OrganizationService organizationService;
    
	public void setPermissionManager(PermissionManager permissionManager) {
		this.permissionManager = permissionManager;
	}
	
	public void setOrganizationService(OrganizationService organizationService) {
		this.organizationService = organizationService;
	}
	

	@RequestMapping(method = RequestMethod.POST)
	public Organization createOrganization(@RequestBody Organization organization) {
		return organizationService.createOrganization(organization);
	}
	
	@RequestMapping(value="/{org_id}", method = RequestMethod.GET)
	public Organization getOrganization(@PathVariable("org_id") String orgId) {
		Organization organization = new Organization();
		organization.setId(orgId);
		return organizationService.getOrganization(organization);
	}
	
	@RequestMapping(value="/{org_id}/apibundle/{api_bundle_id}/permissions", method = RequestMethod.POST)
	public void createApiBundlePermissions(@RequestBody PermissionModel permissionModel, @PathVariable("org_id") String orgId, @PathVariable("api_bundle_id") String apiBundleId) {
		Organization organization = organizationService.getOrganization(orgId);
		if(permissionModel.isGrant()) {
			permissionManager.createAclWithPermissionsAndOwner(ApiBundle.class, apiBundleId, organization, permissionModel.getPermission());			
		} else {
			permissionManager.createAclWithPermissionsAndOwner(ApiBundle.class, apiBundleId, organization, permissionModel.getPermission());
		}
	}
	
}
