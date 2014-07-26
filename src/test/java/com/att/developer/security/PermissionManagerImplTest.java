package com.att.developer.security;

import java.util.ArrayList;
import java.util.List;




import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.Sid;
import org.springframework.transaction.support.TransactionTemplate;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.OrganizationBuilder;
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
	
	
	@Test
	public void testRemovePermissions(){
		String orgIdGrantedAuthority = "ID_ORGANIZATION_GRANTED_AUTHORITY";
		Organization org = new OrganizationBuilder().withId(orgIdGrantedAuthority).build();
		
		GrantedAuthoritySid grantedSid = new GrantedAuthoritySid(orgIdGrantedAuthority);
		List<Sid> sids = new ArrayList<>();
		sids.add(grantedSid);
		
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class), Mockito.eq(sids))).thenReturn(mockAclImpl);
		
		AccessControlEntryImpl acesPrincipal1 = new AccessControlEntryImpl(9, mockAclImpl, new PrincipalSid("somePrincipal1"), BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesGranted = new AccessControlEntryImpl(9, mockAclImpl, grantedSid, BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesOtherGranted = new AccessControlEntryImpl(9, mockAclImpl, new GrantedAuthoritySid("someOtherGrantedAuthority"), BasePermission.READ, true, false, false);
		
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(acesPrincipal1);
		aces.add(acesGranted);
		aces.add(acesOtherGranted);

		Mockito.when(mockAclImpl.getEntries()).thenReturn(aces);
		
		permissionMgr.removeAllPermissionForObject(ApiBundle.class, "UniqueBundleIdentifier", org);
		
//		Assert.assertEquals("", 1, aces.size());
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
