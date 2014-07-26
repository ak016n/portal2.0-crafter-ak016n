package com.att.developer.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.User;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.ApiBundleService;

@Service
@Transactional
public class ApiBundleServiceImpl implements ApiBundleService {
	
	private final Logger logger = LogManager.getLogger();
	
	@Resource
	private ApiBundleDAO apiBundleDAO;
	
	@Resource
	private PermissionManager permissionManager;
	
	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	@Override
	public ApiBundle getApiBundle(String id) {
		logger.debug("getting for id {} ", id);
		List<AccessControlEntry> accessControlEntries = this.permissionManager.getAccessControlEntries(ApiBundle.class, id);
		ApiBundle loadedBundle = apiBundleDAO.load(new ApiBundle(id));
		loadedBundle.setAccessControleEntries(accessControlEntries);
		return loadedBundle;
	}

	@Override
	public List<ApiBundle> getAll() {
		List<ApiBundle> apiBundles = apiBundleDAO.getAll();
		if(apiBundles != null){
			for(ApiBundle bundle : apiBundles){
				List<AccessControlEntry> accessControlEntries = this.permissionManager.getAccessControlEntries(ApiBundle.class, bundle.getId());
				bundle.setAccessControleEntries(accessControlEntries);
			}
		}
		return apiBundles;
	}

	@Override
	public ApiBundle create(ApiBundle bundle, User user) {
		logger.debug("trying to create the bundle " + bundle);
		Assert.notNull(user, "User cannot be null when creating bundle");
		
		permissionManager.createAclWithPermissionsAndOwner(bundle.getClass(), bundle.getId(), user, BasePermission.ADMINISTRATION);
		return apiBundleDAO.create(bundle);
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
	

	/**
	 * Grants default permissions (READ and WRITE) to an Organization for the ApiBundle.
	 */
	@Override
	public void grantPermission(ApiBundle apiBundle, Organization org) {
		//TODO: put in 'strict' switch to toggle on or off  
		//load bundle to make sure it really exists
		ApiBundle reloadedBundle = apiBundleDAO.load(apiBundle);
		Assert.notNull(reloadedBundle, "apiBundle passed in was not found in database, do *not* grant permissions to it. id : " + apiBundle.getId());
		
		permissionManager.grantPermissions(ApiBundle.class, apiBundle.getId(), org, new CumulativePermission().set(BasePermission.WRITE).set(BasePermission.READ));
	}
	
	
	@Override
	public void removeAllPermissions(ApiBundle apiBundle, Organization org){
		ApiBundle reloadedBundle = apiBundleDAO.load(apiBundle);
		Assert.notNull(reloadedBundle, "apiBundle passed in was not found in database, do *not* grant permissions to it. id : " + apiBundle.getId());
		
		permissionManager.removeAllPermissionForObject(ApiBundle.class, apiBundle.getId(), org);
		
	}
}