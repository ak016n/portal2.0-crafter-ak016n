package com.att.developer.service;

import com.att.developer.bean.LoginSecurityDetails;

public interface LoginSecurityService {

	public abstract LoginSecurityDetails getLoginSecurityDetails(String loginCred);
	public abstract void addSecurityDelay();

}