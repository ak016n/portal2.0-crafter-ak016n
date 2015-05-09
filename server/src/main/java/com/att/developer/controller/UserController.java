package com.att.developer.controller;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.PermissionModel;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.User;
import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiWrapper;
import com.att.developer.exception.ServerSideException;
import com.att.developer.security.PermissionManager;
import com.att.developer.security.impl.AuthenticationUtil;
import com.att.developer.service.ApiService;
import com.att.developer.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

    @Inject
    private UserService userService;
    
    @Inject
    private ApiService apiService;
    
    @Resource
    private PermissionManager permissionManager;
    
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	@RequestMapping(method = RequestMethod.POST)
	public User createUser(@RequestBody User user) {
		return userService.createUser(user);
	}
	
	@RequestMapping(value="/{user_id}", method = RequestMethod.GET)
	public User getUser(@PathVariable("user_id") String userId) {
		return userService.getUser(userId);
	}
	
	@RequestMapping(value="/{user_id}/apis", method = RequestMethod.GET)
	public @ResponseBody List<Api> getApisAssocUser(@PathVariable("user_id") String userId) {
		User user = userService.getUser(userId);
		
		Authentication auth = AuthenticationUtil.buildUserAuthentication(user);
		
		List<ApiWrapper> apiWrapperColl = apiService.getApis(auth);
		
		List<Api> apiColl = new ArrayList<>();
		for(ApiWrapper apiWrapper: apiWrapperColl) {
			apiColl.add(apiWrapper.getApi());
		}
		
		return apiColl;
	}
	
	@RequestMapping(value="/{user_id}/apis/{api_id}", method = RequestMethod.POST)
	public void createApiPermissions(@PathVariable("user_id") String userId, @PathVariable("api_id") String apiId, @RequestBody PermissionModel permissionModel) {
		User user = userService.getUser(userId);
		
		if(apiService.isApiWrapperExist(apiId)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("API doesnot exist.").build();
			throw new ServerSideException(error);
		}
		
		String sid = AuthenticationUtil.getUserSid(user);
		permissionModel.setPrincipalSid(sid);
		
		if(permissionModel.isGrant()) {
			permissionManager.createAclWithPermissions(ApiWrapper.class, apiId, permissionModel.getSid() , permissionModel.getPermission());
		} else {
			permissionManager.createAclWithDenyPermissions(ApiWrapper.class, apiId, permissionModel.getSid() , permissionModel.getPermission());
		}
		
	}
	
}
