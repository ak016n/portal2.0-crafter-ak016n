package com.att.developer.controller;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.Organization;
import com.att.developer.bean.PermissionModel;
import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.service.ApiService;
import com.att.developer.service.OrganizationService;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    @Inject
    private OrganizationService organizationService;
    
    @Inject
    private ApiService apiService;
    
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
	
	@RequestMapping(value="/{org_id}/apis", method = RequestMethod.GET)
	public @ResponseBody List<Api> getApisAssocUser(@RequestBody PermissionModel permissionModel, @PathVariable("org_id") String orgId) {
		List<ApiWrapper> apiWrapperColl = apiService.getApis();
		
		List<Api> apiColl = new ArrayList<>();
		for(ApiWrapper apiWrapper: apiWrapperColl) {
			apiColl.add(apiWrapper.getApi());
		}
		
		return apiColl;
	}
	
}
