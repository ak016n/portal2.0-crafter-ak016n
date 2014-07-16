package com.att.developer.service.impl;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.User;
import com.att.developer.service.UserService;

@Component
public class LoginSecurityServiceImpl implements LoginSecurityService {

	private static Logger logger = Logger.getLogger(LoginSecurityServiceImpl.class);

    @Resource
    private UserService userService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

	@Override
    public LoginSecurityDetails getLoginSecurityDetails(String loginCred) {

        LoginSecurityDetails loginSecurityDetails = new LoginSecurityDetails();
        
        User portalUser = getUser(loginCred);

        // AddUser
        loginSecurityDetails.setUser(portalUser);
        
        return loginSecurityDetails;
    }

    private User getUser(String loginCred) {
        User portalUser = null;
        
        try {
            portalUser = userService.getUserByLogin(loginCred);
        } catch (Exception e) {
            if(logger.isDebugEnabled()) {
            	logger.debug("GetUserByLogin : ", e);
            }
            // ok to swallow exception - throw new UsernameNotFoundException(e.getMessage());
        }
        
        try {
            if(portalUser == null) {
                //try email
                portalUser = userService.getUserByEmail(loginCred);
            }
        } catch (Exception e) {
            if(logger.isDebugEnabled()) {
            	logger.debug("GetUserByEmail : ", e);
            }
            // ok to swallow exception - throw new UsernameNotFoundException(e.getMessage());
        }
        return portalUser;
    }

    
}
