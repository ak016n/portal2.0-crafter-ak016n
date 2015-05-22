package com.att.developer.service;

import com.att.developer.bean.LoginSecurityDetails;

public interface LoginSecurityService {

	LoginSecurityDetails getLoginSecurityDetails(String loginCred);
	void addSecurityDelay();

}