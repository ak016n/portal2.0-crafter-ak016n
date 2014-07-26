package com.att.developer.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.typelist.UserStateType;

public class UserDetailsServiceImplTest {

    @Mock
    private LoginSecurityService mockLoginSecurityService;
	
    UserDetailsServiceImpl userDetailsServiceImpl = null;
    
    @Before
    public void init() {
    	MockitoAnnotations.initMocks(this);
    	userDetailsServiceImpl = new UserDetailsServiceImpl();
    	
    	userDetailsServiceImpl.setLoginSecurityService(mockLoginSecurityService);
    }
    
	@Test(expected=UsernameNotFoundException.class)
	public void testLoadByUsername_pendingUser() {
		LoginSecurityDetails loginSecurityDetails = new LoginSecurityDetails(false, false, null, 0, new UserBuilder().withState(UserStateType.PENDING).build());
		
		Mockito.when(mockLoginSecurityService.getLoginSecurityDetails("sheldon")).thenReturn(loginSecurityDetails);
		userDetailsServiceImpl.loadUserByUsername("sheldon");
	}
	
	@Test(expected=LockedException.class)
	public void testLoadByUsername_lockedUser() {
		LoginSecurityDetails loginSecurityDetails = new LoginSecurityDetails(true, false, null, 0, new UserBuilder().withState(UserStateType.PENDING).build());
		
		Mockito.when(mockLoginSecurityService.getLoginSecurityDetails("sheldon")).thenReturn(loginSecurityDetails);
		userDetailsServiceImpl.loadUserByUsername("sheldon");
	}
	
	@Test(expected=BadCredentialsException.class)
	public void testLoadByUsername_warnAccountLock() {
		LoginSecurityDetails loginSecurityDetails = new LoginSecurityDetails(false, true, null, 0, new UserBuilder().withState(UserStateType.PENDING).build());
		
		Mockito.when(mockLoginSecurityService.getLoginSecurityDetails("sheldon")).thenReturn(loginSecurityDetails);
		userDetailsServiceImpl.loadUserByUsername("sheldon");
	}

	@Test
	public void testLoadByUsername_happyPath() {
		User user = new UserBuilder().build();
		LoginSecurityDetails loginSecurityDetails = new LoginSecurityDetails(false, false, null, 0, user);
		
		Mockito.when(mockLoginSecurityService.getLoginSecurityDetails("sheldon")).thenReturn(loginSecurityDetails);
		UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername("sheldon");
		
		Assert.assertTrue(userDetails instanceof SessionUser);
		Assert.assertEquals(user.getId(), ((SessionUser) userDetails).getUsername());
		
	}
}
