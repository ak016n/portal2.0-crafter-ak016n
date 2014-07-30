package com.att.developer.service.impl;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.bean.LoginSecurityDetails;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.UserService;

public class LoginSecurityServiceImplTest {

    @Mock
    private UserService mockUserService;
    
	@Mock
	private EventTrackingService mockEventTrackingService;
	
	@InjectMocks
	LoginSecurityServiceImpl loginSecurityService = null;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetLoginSecurityDetails_withLogin() {
		User user = new UserBuilder().build();
		
		Mockito.when(mockUserService.getUserByLogin("sheldon")).thenReturn(user);
		
		LoginSecurityDetails loginSecurityDetails = loginSecurityService.getLoginSecurityDetails("sheldon");
		
		Assert.assertNotNull(loginSecurityDetails);
		Assert.assertEquals(user, loginSecurityDetails.getUser());
		Mockito.verify(mockUserService, Mockito.atMost(1)).getUserByLogin("sheldon");
		Mockito.verify(mockUserService, Mockito.never()).getUserByEmail("sheldon");
	}
	
	@Test
	public void testGetLoginSecurityDetails_withEmail() {
		User user = new UserBuilder().build();
		
		Mockito.when(mockUserService.getUserByEmail("sheldon@att.com")).thenReturn(user);
		
		LoginSecurityDetails loginSecurityDetails = loginSecurityService.getLoginSecurityDetails("sheldon@att.com");
		
		Assert.assertNotNull(loginSecurityDetails);
		Assert.assertEquals(user, loginSecurityDetails.getUser());
		Mockito.verify(mockUserService, Mockito.atMost(1)).getUserByLogin("sheldon@att.com");
		Mockito.verify(mockUserService, Mockito.atMost(1)).getUserByEmail("sheldon@att.com");
	}
}
