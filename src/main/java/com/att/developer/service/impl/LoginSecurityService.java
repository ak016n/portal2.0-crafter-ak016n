package com.att.developer.service.impl;

import com.att.developer.bean.LoginSecurityDetails;

public interface LoginSecurityService {

	public abstract LoginSecurityDetails getLoginSecurityDetails(
			String loginCred);

}