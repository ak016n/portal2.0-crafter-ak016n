package com.att.developer.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.service.LoginSecurityService;
import com.att.developer.service.UserCreator;
import com.att.developer.typelist.UserStateType;

@Component("attUserDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService, UserCreator {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
    
    @Autowired
    private LoginSecurityService loginSecurityService;

    public void setLoginSecurityService(LoginSecurityService loginSecurityService) {
        this.loginSecurityService = loginSecurityService;
    }

    @Override
    public UserDetails loadUserByUsername(String loginCred) throws UsernameNotFoundException {

        LoginSecurityDetails loginSecurityDetails = loginSecurityService.getLoginSecurityDetails(loginCred);

        throwExceptionOnMeetingAcctLockCondition(loginSecurityDetails);
        
        User user = loginSecurityDetails.getUser();

        if (user == null || user.hasUserState(UserStateType.PENDING)
                || user.hasUserState(UserStateType.INACTIVE)) {
            throw new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE);
        }
        
        return SessionUser.buildSecurityUser(user);
    }

    public void throwExceptionOnMeetingAcctLockCondition(LoginSecurityDetails loginSecurityDetails) {
        if (loginSecurityDetails.isAccountLocked()) {
            throw new LockedException(loginSecurityDetails.getErrorMessage());
        }

        if (loginSecurityDetails.isWarnAccountLock()) {
            throw new BadCredentialsException(loginSecurityDetails.getErrorMessage());
        }
    }

	@Override
	public SessionUser buildSessionUserFromUserEntity(User portalUser) {
		return (SessionUser) SessionUser.buildSecurityUser(portalUser);
	}

}
