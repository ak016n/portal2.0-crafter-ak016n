package com.att.developer.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.User;
import com.att.developer.bean.UserState;
import com.att.developer.typelist.UserStateType;

public class UserDetailsServiceImpl implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found.";
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
        
        return buildUserFromUserEntity(user);
    }

    public void throwExceptionOnMeetingAcctLockCondition(LoginSecurityDetails loginSecurityDetails) {
        if (loginSecurityDetails.isAccountLocked()) {
            throw new LockedException(loginSecurityDetails.getErrorMessage());
        }

        if (loginSecurityDetails.isWarnAccountLock()) {
            throw new BadCredentialsException(loginSecurityDetails.getErrorMessage());
        }
    }

    /*
     * Build Java user principal
     */
    private org.springframework.security.core.userdetails.User buildUserFromUserEntity(User portalUser) {
        String username = portalUser.getLogin();
        String password = portalUser.getEncryptedPassword();
        // There to see if we have to use any information from actual user object to determine the state of others
        boolean enabled = true;
        boolean accountNonExpired = enabled;
        boolean credentialsNonExpired = enabled;
        boolean accountNonLocked = enabled;

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        for (UserState userState : portalUser.getUserStates()) {
            authorities.add(new SimpleGrantedAuthority(userState.getState().name()));
        }
        
        //TODO hierarchical organization state
        org.springframework.security.core.userdetails.User user = new org.springframework.security.core.userdetails.User(
                username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return user;
    }

}
