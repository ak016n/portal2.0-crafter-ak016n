package com.att.developer.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.UserState;
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
        String userId = portalUser.getId();
        String password = portalUser.getEncryptedPassword();
        // There to see if we have to use any information from actual user object to determine the state of others

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        
        for (UserState userState : portalUser.getUserStates()) {
            authorities.add(new SimpleGrantedAuthority(userState.getState().name()));
        }
        
        addRoles(authorities, portalUser.getRoles());
        
        List<Organization> orgs = portalUser.getOrganizations();
        
        addOrgAuthority(authorities, orgs);

        //Note:  We are using userId which is a generated UUID for the 'username' that Spring requires.
        //TODO hierarchical organization state
        SessionUser user = new SessionUser(userId, password, authorities, portalUser);
        return user;
    }

	private void addOrgAuthority(Collection<GrantedAuthority> authorities, List<Organization> orgs) {
		if(orgs != null) {
	        for(Organization org : orgs){
	        	authorities.add(new SimpleGrantedAuthority(org.getId()));
	        }
		}
	}

	private void addRoles(Collection<GrantedAuthority> authorities, Set<Role> roles) {
		if(roles != null) {
	        for(Role role : roles) {
	        	authorities.add(new SimpleGrantedAuthority(role.getName()));
	        }
		}
	}
	
	@Override
	public SessionUser buildSessionUserFromUserEntity(User portalUser){
	    return (SessionUser)this.buildUserFromUserEntity(portalUser);
	}
}
