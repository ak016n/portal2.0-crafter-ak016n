package com.att.developer.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.log4j.Logger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.stereotype.Service;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.User;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.ApiBundleService;

@Service
@Transactional
public class ApiBundleServiceImpl implements ApiBundleService {
	
	private static final Logger logger = Logger.getLogger(ApiBundleServiceImpl.class);
	
	@Resource
	private ApiBundleDAO apiBundleDAO;
	
	@Resource
	private PermissionManager permissionManager;
	
	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	@Override
	public ApiBundle getSingle(String id) {
		return apiBundleDAO.load(new ApiBundle(id));
	}

	@Override
	public List<ApiBundle> getAll() {
		// TODO Implement
		return null;
	}

	@Override
	public ApiBundle create(ApiBundle bean, User user) {
		logger.debug("trying to create the bundle " + bean);
		
		permissionManager.createAclWithPermissionsAndOwner(bean.getClass(), bean.getId(), user.getId(), BasePermission.ADMINISTRATION, user.getId());
		return apiBundleDAO.create(bean);
	}

	@Override
	public ApiBundle edit(ApiBundle apiBundle) {
		return apiBundleDAO.update(apiBundle);
	}

	@Override
	public void delete(ApiBundle apiBundle) {
		permissionManager.deletePermissionsForObject(apiBundle.getClass(), apiBundle.getId());
		apiBundleDAO.delete(apiBundle);
	}
	

}
