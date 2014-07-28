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
	public void testCreateAcl() {
		Role adminRole = new RoleBuilder().withName(Role.ROLE_NAME_SYS_ADMIN).build();
		User ownerAndPermissionHolderUser = new UserBuilder().withRole(adminRole).build();
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class))).thenReturn(mockAclImpl);
		permissionMgr.createAclWithPermissionsAndOwner(ApiBundle.class, "UniqueBundleIdentifier", ownerAndPermissionHolderUser, BasePermission.ADMINISTRATION);
		Mockito.verify(mockMutableAclService, Mockito.times(1)).createAcl(Mockito.any(ObjectIdentity.class));
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
		

		AclAuthorizationStrategy aclAuthorizationStrategy = new AclAuthorizationStrategyImpl(new SimpleGrantedAuthority(Role.ROLE_NAME_SYS_ADMIN));
		
		Acl parentAcl = null;
		List<Sid> loadedSids = null;
		boolean entriesInheriting = false;
		
		Sid owner = new GrantedAuthoritySid(Role.ROLE_NAME_SYS_ADMIN);
		AclImpl aclImpl = new  AclImpl(new ObjectIdentityImpl(ApiBundle.class, "someIdentifier"), "someAclPrimaryKey", aclAuthorizationStrategy, mockPermissionGrantingStrategy, parentAcl, loadedSids, entriesInheriting, owner);
		
		Mockito.when(mockMutableAclService.readAclById(Mockito.any(ObjectIdentity.class), Mockito.eq(sids))).thenReturn(aclImpl);
		
		AccessControlEntryImpl acesPrincipal1 = new AccessControlEntryImpl(9, aclImpl, new PrincipalSid("somePrincipal1"), BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesGranted = new AccessControlEntryImpl(9, aclImpl, grantedSid, BasePermission.READ, true, false, false);
		AccessControlEntryImpl acesOtherGranted = new AccessControlEntryImpl(9, aclImpl, new GrantedAuthoritySid("someOtherGrantedAuthority"), BasePermission.READ, true, false, false);
		
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(acesPrincipal1);
		aces.add(acesGranted);
		aces.add(acesOtherGranted);
		

		aclImpl.insertAce(0, BasePermission.READ, acesPrincipal1.getSid(), true);
		aclImpl.insertAce(1, BasePermission.READ, acesGranted.getSid(), true);
		aclImpl.insertAce(2, BasePermission.READ, acesOtherGranted.getSid(), true);

		permissionMgr.removeAllPermissionForObject(ApiBundle.class, "UniqueBundleIdentifier", org);
		List<AccessControlEntry> acesActual = aclImpl.getEntries();
		
		Assert.assertEquals("Should have been 2 permissions left in the Acl.", 2, acesActual.size());
		
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
