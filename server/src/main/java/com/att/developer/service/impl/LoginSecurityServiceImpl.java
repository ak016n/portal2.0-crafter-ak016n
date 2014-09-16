package com.att.developer.service.impl;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.User;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.LoginSecurityService;
import com.att.developer.service.UserService;

@Component
public class LoginSecurityServiceImpl implements LoginSecurityService {

	private final Logger logger = LogManager.getLogger();
	
	
    private static final int LOGIN_DELAY_RANGE_START = 500;
    private static final int LOGIN_DELAY_RANGE_END = 1000;

    @Resource
    private UserService userService;
    
	@Autowired
	private EventTrackingService eventTrackingService;

    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
	public void setEventTrackingService(EventTrackingService eventTrackingService) {
		this.eventTrackingService = eventTrackingService;
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

    /**
     * This security delay is on when provided incorrect credentials
     */
    public void addSecurityDelay() {
        try {
            Thread.sleep(generateRandomNumber(LOGIN_DELAY_RANGE_START, LOGIN_DELAY_RANGE_END));
        } catch (InterruptedException e) {
            // ok to not catch this one
        }
    }
    
    /**
     * Generates a random number in the given range
     */
    private int generateRandomNumber(int min, int max) {
        int randomNumber = (int) (Math.random() * (max - min + 1)) + min;
        return randomNumber;
    }
}
