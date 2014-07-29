package com.att.developer.service.impl;


import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.PermissionManager;

public class ApiBundleServiceImplTest {

	private ApiBundleServiceImpl apiBundleService;
	
	private static final String UNIQUE_BUNDLE_ID = "someUniqueId";
	
	
	@Mock
	private ApiBundleDAO mockApiBundleDAO;

	@Mock
	private PermissionManager mockPermissionManager;
	
	
	@Before
	public void before(){
		MockitoAnnotations.initMocks(this);
		apiBundleService = new ApiBundleServiceImpl();
		apiBundleService.setApiBundleDAO(mockApiBundleDAO);
		
		apiBundleService.setPermissionManager(mockPermissionManager);
		
		Authentication authRequest = new UsernamePasswordAuthenticationToken("rod", "koala", AuthorityUtils.createAuthorityList(Role.ROLE_NAME_SYS_ADMIN));
		SecurityContextHolder.getContext().setAuthentication(authRequest);
	}
	
	
	@Test
	public void testGetApiBundle() {
		ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
		Mockito.when(mockApiBundleDAO.load(Mockito.any(ApiBundle.class))).thenReturn(initialBundle);
		ApiBundle actualBundle = apiBundleService.getApiBundle(UNIQUE_BUNDLE_ID);
		Mockito.verify(mockApiBundleDAO, Mockito.times(1)).load(Mockito.any(ApiBundle.class));
		Mockito.verify(mockPermissionManager, Mockito.times(1)).getAccessControlEntries(Mockito.eq(ApiBundle.class), Mockito.eq(UNIQUE_BUNDLE_ID));
		Assert.assertEquals(initialBundle, actualBundle);
	}

	
	@Test
	public void testGetAll() {
		ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
		List<ApiBundle> listBundles = new ArrayList<>();
		listBundles.add(initialBundle);
		Mockito.when(mockApiBundleDAO.getAll()).thenReturn(listBundles);
		List<ApiBundle> apiBundles = apiBundleService.getAll();
		Assert.assertTrue(apiBundles.contains(initialBundle));
		Mockito.verify(mockApiBundleDAO, Mockito.times(1)).getAll();
		Mockito.verify(mockPermissionManager, Mockito.times(1)).getAccessControlEntries(Mockito.eq(ApiBundle.class), Mockito.eq(UNIQUE_BUNDLE_ID));
	}

	
	
	/**
	 * Mostly tests we are calling correct underlying methods and that Administration permissions assigned.
	 */
	@Test
	public void testCreate() {
		ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
		User actor = new UserBuilder().build();
		Mockito.when(mockApiBundleDAO.create(Mockito.any(ApiBundle.class))).thenReturn(initialBundle);
		ApiBundle createdBundle = apiBundleService.create(initialBundle, actor);
		Assert.assertNotNull(createdBundle);
		Mockito.verify(mockPermissionManager, Mockito.times(1)).createAclWithPermissionsAndOwner(Mockito.eq(ApiBundle.class), Mockito.eq(UNIQUE_BUNDLE_ID), Mockito.eq(actor), Mockito.eq(BasePermission.ADMINISTRATION));
		Mockito.verify(mockApiBundleDAO, Mockito.times(1)).create(Mockito.eq(initialBundle));
		
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testCreate_noUser() {
		ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
		User actor = null;
		apiBundleService.create(initialBundle, actor);
	}


	/*
	@Test
	public void testEdit() {
		fail("Not yet implemented");
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testGrantPermission() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAllPermissions() {
		fail("Not yet implemented");
	}
*/
}
