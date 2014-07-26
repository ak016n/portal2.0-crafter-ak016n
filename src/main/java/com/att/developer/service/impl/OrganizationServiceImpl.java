package com.att.developer.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import com.att.developer.bean.Organization;
import com.att.developer.dao.OrganizationDAO;
import com.att.developer.service.OrganizationService;

@Component
public class OrganizationServiceImpl implements OrganizationService {

	@Resource
	private OrganizationDAO orgDAO;

	@Override
	public Organization getOrganization(Organization org) {
		return orgDAO.load(org);
	}
}
