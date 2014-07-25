package com.att.developer.security;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.transaction.support.TransactionTemplate;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;


public class PermissionManagerImplTest {

	
	//under test
	private PermissionManagerImpl permissionMgr;
	
	
	@Mock
	private TransactionTemplate mockTransactionTemplate;
	
	@Mock
	private MutableAclService mockMutableAclService;
	
	@Mock
	private AclImpl mockAclImpl;
	
	@Mock
	private AccessControlEntry mockAccessControlEntry;
	
	
	@Before
	public void before(){
		MockitoAnnotations.initMocks(this);
		permissionMgr = new PermissionManagerImpl();
		permissionMgr.setTransactionTemplate(mockTransactionTemplate);
		permissionMgr.setMutableAclService(mockMutableAclService);
	}
	
	@Test
	public void testCreateAcl() {
		Role adminRole = new RoleBuilder().withName(Role.ROLE_NAME_SYS_ADMIN).build();
		User ownerAndPermissionHolderUser = new UserBuilder().withRole(adminRole).build();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(mockAclImpl);
		permissionMgr.createAclWithPermissionsAndOwner(ApiBundle.class, "UniqueBundleIdentifier", ownerAndPermissionHolderUser, BasePermission.ADMINISTRATION);
		
		//TODO: Finish verifying mocks called.
//		Assert.fail("Not yet implemented");
	}

/*
	@Test
	public void testGrantPermissionsClassOfQStringUserPermission() {
		fail("Not yet implemented");
	}

	@Test
	public void testGrantPermissionsClassOfQStringOrganizationPermission() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeOwnerClassOfQStringUser() {
		fail("Not yet implemented");
	}

	@Test
	public void testChangeOwnerClassOfQStringOrganization() {
		fail("Not yet implemented");
	}

	@Test
	public void testDeletePermissionsForObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testRemoveAllPermissionForObject() {
		fail("Not yet implemented");
	}

	@Test
	public void testGetAccessControlEntries() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateAclWithPermissionsAndOwnerClassOfQStringUserPermission() {
		fail("Not yet implemented");
	}

	@Test
	public void testCreateAclWithPermissionsAndOwnerClassOfQStringOrganizationPermission() {
		fail("Not yet implemented");
	}
*/
}
