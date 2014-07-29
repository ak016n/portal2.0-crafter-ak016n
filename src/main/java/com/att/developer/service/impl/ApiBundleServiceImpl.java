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
import com.att.developer.bean.EventLog;
import com.att.developer.bean.Organization;
import com.att.developer.bean.User;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.ApiBundleService;
import com.att.developer.service.EventTrackingService;
import com.att.developer.typelist.ActorType;
import com.att.developer.typelist.EventType;

@Service
@Transactional
public class ApiBundleServiceImpl implements ApiBundleService {
	
	private final Logger logger = LogManager.getLogger();
	
	@Resource
	private ApiBundleDAO apiBundleDAO;
	
	@Resource
	private PermissionManager permissionManager;

	@Resource
	private EventTrackingService eventTrackingService;
	
	
	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	
	public void setPermissionManager(PermissionManager manager) {
		this.permissionManager = manager;
	}
	
	
	public void setEventTrackingService(EventTrackingService service) {
		this.eventTrackingService = service;
	}
	
	
	@Override
	public ApiBundle getApiBundle(String id) {
		logger.debug("getting for id {} ", id);
		ApiBundle loadedBundle = apiBundleDAO.load(new ApiBundle(id));
		if(loadedBundle != null){
			List<AccessControlEntry> accessControlEntries = this.permissionManager.getAccessControlEntries(ApiBundle.class, id);
			loadedBundle.setAccessControleEntries(accessControlEntries);
		}
		return loadedBundle;
	}

	/**
	 * TODO: if we ever have a huge list of Bundles (think thousands) we may 
	 * want to revisit this to not get the AccessControlEntry List for each bundle one at a time.
	 * 
	 * Some direct JDBC call to the Spring ACL tables would be faster
	 */
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
		permissionManager.deleteAllPermissionsForObject(apiBundle.getClass(), apiBundle.getId());
		apiBundleDAO.delete(apiBundle);
	}
	

	/**
	 * Grants default permissions (READ and WRITE) to an Organization for the ApiBundle.
	 */
	@Override
	public void grantPermission(ApiBundle apiBundle, Organization org, User actor) {
		//TODO: put in 'strict' switch to toggle on or off  
		//load bundle to make sure it really exists
		Assert.notNull(actor, "User actor required to grant permissions");
		ApiBundle reloadedBundle = apiBundleDAO.load(apiBundle);
		Assert.notNull(reloadedBundle, "apiBundle passed in was not found in database, do *not* grant permissions to it. id : " + apiBundle.getId());
		CumulativePermission permission = new CumulativePermission().set(BasePermission.WRITE).set(BasePermission.READ);
		permissionManager.grantPermissions(ApiBundle.class, apiBundle.getId(), org, permission);
		String info = "granting to ApiBundle id : " + apiBundle.getId() + " to Organization id : " + org.getId() + "  permissions " + permission;
		EventLog eventLog = new EventLog(actor.getId(), null, org.getId(), EventType.API_BUNDLE_PERMISSION_UPDATED, info, ActorType.DEV_PROGRAM_USER, null);
		eventTrackingService.writeEvent(eventLog);
	}
	
	
	@Override
	public void removeAllPermissions(ApiBundle apiBundle, Organization org, User actor){
		ApiBundle reloadedBundle = apiBundleDAO.load(apiBundle);
		Assert.notNull(reloadedBundle, "apiBundle passed in was not found in database, do *not* grant permissions to it. id : " + apiBundle.getId());
		permissionManager.removeAllPermissionForObjectForOrganization(ApiBundle.class, apiBundle.getId(), org);
		String info = "removing from ApiBundle id : " + apiBundle.getId() + " to Organization id : " + org.getId() + "  all permissions";
		EventLog eventLog = new EventLog(actor.getId(), null, org.getId(), EventType.API_BUNDLE_PERMISSION_UPDATED, info, ActorType.DEV_PROGRAM_USER, null);
		eventTrackingService.writeEvent(eventLog);
	}


}