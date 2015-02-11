package com.att.developer.service.portal.one;

import java.util.List;

import com.att.developer.bean.User;

public interface UserProfileService {

	public List<String> getUserPermissions(String login);
	
	public User getUser(String login);
	
}
