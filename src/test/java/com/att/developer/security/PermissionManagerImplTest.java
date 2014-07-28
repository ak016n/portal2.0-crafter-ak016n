package com.att.developer.security;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclAuthorizationStrategy;
import org.springframework.security.acls.domain.AclAuthorizationStrategyImpl;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.ObjectIdentityImpl;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.Acl;
import org.springframework.security.acls.model.MutableAcl;
import org.springframework.security.acls.model.MutableAclService;
import org.springframework.security.acls.model.ObjectIdentity;
import org.springframework.security.acls.model.PermissionGrantingStrategy;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.support.SimpleTransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.service.OrganizationService;
import com.att.developer.service.UserService;


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
	
	@Mock
	private PermissionGrantingStrategy mockPermissionGrantingStrategy;
	
	

	@Before
	public void before(){
		MockitoAnnotations.initMocks(this);
		permissionMgr = new PermissionManagerImpl();
		permissionMgr.setTransactionTemplate(mockTransactionTemplate);
		permissionMgr.setMutableAclService(mockMutableAclService);
		
		Mockito.when(mockTransactionTemplate.execute(Mockito.<TransactionCallback<?>> any())).thenAnswer(new Answer<Object>() {
			public Object answer(InvocationOnMock invocation) {
				Object[] args = invocation.getArguments();
				@SuppressWarnings("unchecked")
				TransactionCallback<Object> arg = (TransactionCallback<Object>)args[0];
				return arg.doInTransaction(new SimpleTransactionStatus());
			}
		});
		
		Authentication authRequest = new UsernamePasswordAuthenticationToken("rod", "koala", AuthorityUtils.createAuthorityList(Role.ROLE_NAME_SYS_ADMIN));
		SecurityContextHolder.getContext().setAuthentication(authRequest);
	}
	
	
	@Test
	public void testCreateAcl(){
		permissionMgr.createAcl(ApiBundle.class, "UniqueBundleIdentifier");
		Mockito.verify(mockMutableAclService, Mockito.times(1)).createAcl(Mockito.any(ObjectIdentity.class));
	}
	
	
	@Test
	public void testCreateAclWithPermissionsAndOwnerForUser() {
		Role adminRole = new RoleBuilder().withName(Role.ROLE_NAME_SYS_ADMIN).build();
		User ownerAndPermissionHolderUser = new UserBuilder().withRole(adminRole).build();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(mockAclImpl);
		permissionMgr.createAclWithPermissionsAndOwner(ApiBundle.class, "UniqueBundleIdentifier", ownerAndPermissionHolderUser, BasePermission.ADMINISTRATION);
		Mockito.verify(mockMutableAclService, Mockito.times(1)).createAcl(Mockito.any(ObjectIdentity.class));
		//two interactions, one for changing owner, one for granting permission
		Mockito.verify(mockMutableAclService, Mockito.times(2)).updateAcl(Mockito.any(MutableAcl.class));
	}

	@Test
	public void testCreateAclWithPermissionsAndOwnerForOrganization() {
		Organization ownerAndPermissionHolderOrg = new OrganizationBuilder().build();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(mockAclImpl);
		permissionMgr.createAclWithPermissionsAndOwner(ApiBundle.class, "UniqueBundleIdentifier", ownerAndPermissionHolderOrg, BasePermission.ADMINISTRATION);
		Mockito.verify(mockMutableAclService, Mockito.times(1)).createAcl(Mockito.any(ObjectIdentity.class));
		//two interactions, one for changing owner, one for granting permission
		Mockito.verify(mockMutableAclService, Mockito.times(2)).updateAcl(Mockito.any(MutableAcl.class));
	}
	
	@Test
	public void testGrantPermissions_forOrganization(){
		String orgIdGrantedAuthority = "ID_ORGANIZATION_GRANTED_AUTHORITY";
		Organization org = new OrganizationBuilder().withId(orgIdGrantedAuthority).build();
		OrganizationService mockOrgService = Mockito.mock(OrganizationService.class);
		AclImpl aclImpl = this.buildAclImpl();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(aclImpl);
		permissionMgr.setOrganizationService(mockOrgService);
		Mockito.when(mockOrgService.getOrganization(Mockito.any())).thenReturn(org);
		permissionMgr.grantPermissions(ApiBundle.class, "UniqueBundleIdentifier", org, BasePermission.WRITE);
		Mockito.verify(mockMutableAclService, Mockito.times(1)).updateAcl(Mockito.any(MutableAcl.class));
	}
	
	@Test
	public void testGrantPermissions_forOrganizationWithExistingPermission(){
		String orgIdGrantedAuthority = "ID_ORGANIZATION_GRANTED_AUTHORITY";
		Organization org = new OrganizationBuilder().withId(orgIdGrantedAuthority).build();
		OrganizationService mockOrgService = Mockito.mock(OrganizationService.class);
		AclImpl aclImpl = this.buildAclImpl();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(aclImpl);
		permissionMgr.setOrganizationService(mockOrgService);
		Mockito.when(mockOrgService.getOrganization(Mockito.any())).thenReturn(org);
		
		//check previously existing permission
		GrantedAuthoritySid grantedSid = new GrantedAuthoritySid(orgIdGrantedAuthority);
		List<Sid> sids = new ArrayList<>();
		sids.add(grantedSid);
		
		AccessControlEntryImpl acesPrincipal1 = new AccessControlEntryImpl(9, aclImpl, new PrincipalSid("somePrincipal1"), BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesGranted = new AccessControlEntryImpl(9, aclImpl, grantedSid, BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesOtherGranted = new AccessControlEntryImpl(9, aclImpl, new GrantedAuthoritySid("someOtherGrantedAuthority"), BasePermission.READ, true, false, false);
		
		this.insertAces(aclImpl, acesPrincipal1, acesGranted, acesOtherGranted);
		
		permissionMgr.grantPermissions(ApiBundle.class, "UniqueBundleIdentifier", org, BasePermission.READ);
		//we should NEVER insert same permission again...
		Mockito.verify(mockMutableAclService, Mockito.never()).updateAcl(Mockito.any(MutableAcl.class));
	}
	
	
	@Test
	public void testGrantPermissions_forUser(){
		User user = new UserBuilder().build();
		UserService mockUserService = Mockito.mock(UserService.class);
		AclImpl aclImpl = this.buildAclImpl();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(aclImpl);
		permissionMgr.setUserService(mockUserService);
		Mockito.when(mockUserService.getUser(Mockito.any())).thenReturn(user);
		permissionMgr.grantPermissions(ApiBundle.class, "UniqueBundleIdentifier", user, BasePermission.WRITE);
		Mockito.verify(mockMutableAclService, Mockito.times(1)).updateAcl(Mockito.any(MutableAcl.class));
	}
	
	@Test
	public void testRemovePermissionsForObjectForOrganization(){
		String orgIdGrantedAuthority = "ID_ORGANIZATION_GRANTED_AUTHORITY";
		Organization org = new OrganizationBuilder().withId(orgIdGrantedAuthority).build();
		
		GrantedAuthoritySid grantedSid = new GrantedAuthoritySid(orgIdGrantedAuthority);
		List<Sid> sids = new ArrayList<>();
		sids.add(grantedSid);
		
		
		AclImpl aclImpl = this.buildAclImpl();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class), Mockito.eq(sids))).thenReturn(aclImpl);
		
		AccessControlEntryImpl acesPrincipal1 = new AccessControlEntryImpl(9, aclImpl, new PrincipalSid("somePrincipal1"), BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesGranted = new AccessControlEntryImpl(9, aclImpl, grantedSid, BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesOtherGranted = new AccessControlEntryImpl(9, aclImpl, new GrantedAuthoritySid("someOtherGrantedAuthority"), BasePermission.READ, true, false, false);
		
		this.insertAces(aclImpl, acesPrincipal1, acesGranted, acesOtherGranted);


		permissionMgr.removeAllPermissionForObjectForOrganization(ApiBundle.class, "UniqueBundleIdentifier", org);
		List<AccessControlEntry> acesActual = aclImpl.getEntries();
		
		Assert.assertEquals("Should have been 2 permissions left in the Acl.", 2, acesActual.size());
		
		Assert.assertFalse("acesGranted SHOULD have been removed", acesActual.contains(acesGranted));
		
	}
	
	
	@Test
	public void testDeleteAllPermissionsForObject(){
		permissionMgr.deleteAllPermissionsForObject(ApiBundle.class, "UniqueBundleIdentifier");
		Mockito.verify(mockMutableAclService, Mockito.times(1)).deleteAcl(Mockito.any(ObjectIdentity.class), Mockito.eq(false));
	}
	
	
	@Test
	public void testGetAccessControlEntries(){
		AclImpl aclImpl = this.buildAclImpl();
		GrantedAuthoritySid grantedSid = new GrantedAuthoritySid("ID_ORGANIZATION_GRANTED_AUTHORITY");
		this.insertAces(aclImpl, new AccessControlEntryImpl(9, aclImpl, grantedSid, BasePermission.READ, true, false, false));
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(aclImpl);
		List<AccessControlEntry> aces = permissionMgr.getAccessControlEntries(ApiBundle.class, "UniqueBundleIdentifier");
		Assert.assertEquals("should be only ONE ace in list", 1, aces.size());
	}
	
	
	@Test
	public void testChangeOwnerOrganization(){
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(mockAclImpl);
		permissionMgr.changeOwner(ApiBundle.class, "UniqueBundleIdentifier", new OrganizationBuilder().build());
		Mockito.verify(mockMutableAclService, Mockito.times(1)).readAclById(Mockito.any(ObjectIdentity.class));
		Mockito.verify(mockMutableAclService, Mockito.times(1)).updateAcl(Mockito.any(MutableAcl.class));
	}
	
	
	private AclImpl buildAclImpl(){
		AclAuthorizationStrategy aclAuthorizationStrategy = new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(Role.ROLE_NAME_SYS_ADMIN));
		
		Acl parentAcl = null;
		List<Sid> loadedSids = null;
		boolean entriesInheriting = false;
		
		Sid owner = new GrantedAuthoritySid(Role.ROLE_NAME_SYS_ADMIN);
		AclImpl aclImpl = new  AclImpl(new ObjectIdentityImpl(ApiBundle.class, "someIdentifier"), "someAclPrimaryKey", aclAuthorizationStrategy, mockPermissionGrantingStrategy, parentAcl, loadedSids, entriesInheriting, owner);
		
		return aclImpl;
	}
	
	
	
	private AclImpl insertAces(AclImpl aclImpl, AccessControlEntry... aces){
		for(AccessControlEntry ace : aces){
			aclImpl.insertAce(0, ace.getPermission(), ace.getSid(), true);	
		}
		return aclImpl;
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
