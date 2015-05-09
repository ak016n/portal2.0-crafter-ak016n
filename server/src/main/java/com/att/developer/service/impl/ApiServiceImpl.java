package com.att.developer.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.springframework.security.access.prepost.PostFilter;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiBundle;
import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.dao.ApiDAO;
import com.att.developer.dao.ApiWrapperDAO;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.ApiService;

@Component
public class ApiServiceImpl implements ApiService {

	@Resource
    private ApiDAO apiDAO;
    
    @Resource
    private ApiWrapperDAO apiWrapperDAO;

    @Resource
    private ApiBundleDAO apiBundleDAO;
    
    @Inject
    private PermissionManager permissionManager;
    

	public void setApiDAO(ApiDAO apiDAO) {
		this.apiDAO = apiDAO;
	}

	public void setApiWrapperDAO(ApiWrapperDAO apiWrapperDAO) {
		this.apiWrapperDAO = apiWrapperDAO;
	}

	public void setApiBundleDAO(ApiBundleDAO apiBundleDAO) {
		this.apiBundleDAO = apiBundleDAO;
	}

	@Override
	@Transactional
    public Api createApi(Api api) {
    	return apiDAO.create(api);
    }
	
	@Override
	@Transactional
	public ApiBundle createApiBundle(ApiBundle apiBundle) {
		ApiBundle postCreateApiBundle = apiBundleDAO.create(apiBundle);
		Permission p = new CumulativePermission().set(BasePermission.READ);//.set(BasePermission.WRITE);
		permissionManager.createAclWithPermissionsAndOwner(ApiBundle.class, postCreateApiBundle.getId(), postCreateApiBundle.getPermission().getSid(), p);
		
		ObjectIdentity oi = new ObjectIdentityImpl(ApiBundle.class, postCreateApiBundle.getId());
		Sid sid = new GrantedAuthoritySid("API_ACCESS");
		
		for(ApiWrapper apiWrapper : postCreateApiBundle.getApiWrappers()) {
			permissionManager.createAclWithParents(ApiWrapper.class, apiWrapper.getId(), sid, p, sid, oi);
		}
		
		return postCreateApiBundle;
	}
	
	@Override
	public ApiWrapper getApiWrapper(String id) {
		return apiWrapperDAO.load(new ApiWrapper(id));
	}
	
	@Override
	@PostFilter("@runAsSecurityManager.hasPermission(#auth, filterObject, 'read')")
	public List<ApiWrapper> getApis(Authentication auth) {
		return apiWrapperDAO.getAll();
	}
}
