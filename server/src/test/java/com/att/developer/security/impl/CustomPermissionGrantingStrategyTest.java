package com.att.developer.security.impl;



import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.security.acls.domain.AccessControlEntryImpl;
import org.springframework.security.acls.domain.AclImpl;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.CumulativePermission;
import org.springframework.security.acls.domain.GrantedAuthoritySid;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.acls.model.NotFoundException;
import org.springframework.security.acls.model.Permission;
import org.springframework.security.acls.model.Sid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import com.att.developer.security.impl.CustomPermissionGrantingStrategy;
import com.att.developer.security.impl.EventLogAuditLogger;



public class CustomPermissionGrantingStrategyTest {

	
	private CustomPermissionGrantingStrategy customPermissionGrantingStrategy = null;

	
	@Before
	public void initialize(){
        Authentication authRequest = new UsernamePasswordAuthenticationToken("rod", "koala", AuthorityUtils.createAuthorityList("ROLE_IGNORED"));
        SecurityContextHolder.getContext().setAuthentication(authRequest);

		EventLogAuditLogger auditLogger = Mockito.mock(EventLogAuditLogger.class);
		customPermissionGrantingStrategy = new CustomPermissionGrantingStrategy(auditLogger);

	}
	
	
	@Test(expected=NotFoundException.class)
	public void testIsGranted_noMatchingSid() throws Exception{
		
		AclImpl acl = Mockito.mock(AclImpl.class);
		
		AccessControlEntryImpl acesSomasRead = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.READ, true, false, false);
		
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(acesSomasRead);

		Mockito.when(acl.getEntries()).thenReturn(aces);
		
		
		List<Permission> requiredPermissions = new ArrayList<>(); 
		requiredPermissions.add(BasePermission.READ);
		List<Sid> sids = new ArrayList<>(2);
		sids.add(new PrincipalSid("someOtherSid"));
		boolean administrativeMode = false;
		
		
		//someOtherSid should NOT be granted access (Wrong SID), should throw exception
		customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);
	}

	
	@Test
	public void testIsGranted_readPermissionRequired() throws Exception{

		AclImpl acl = Mockito.mock(AclImpl.class);
		
		AccessControlEntryImpl acesSomasWrite = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.WRITE, true, false, false);
		AccessControlEntryImpl acesUnauthorizedUser = new AccessControlEntryImpl(10, acl, new PrincipalSid("authorizedUserRead"), BasePermission.READ, true, false, false);
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(acesSomasWrite);
		aces.add(acesUnauthorizedUser);

		Mockito.when(acl.getEntries()).thenReturn(aces);
		
		
		List<Permission> requiredPermissions = new ArrayList<>(); 
		requiredPermissions.add(BasePermission.READ);
		List<Sid> sids = new ArrayList<>(2);
		sids.add(new PrincipalSid("somas"));
		sids.add(new GrantedAuthoritySid("ROLE_ADMINISTRATOR"));
		
			
		boolean administrativeMode = false;
		boolean isGrantedActual = customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);
		Assert.assertTrue("somas with WRITE privileges should be granted access to a read resource", isGrantedActual);
		
		sids.clear();
		sids.add(new PrincipalSid("authorizedUserRead"));
		
		boolean isGrantedActualAuthorized = customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);
		Assert.assertTrue("authorizedUserRead with READ privileges should be granted access", isGrantedActualAuthorized);
		
	}
	
	
	@Test
	public void testIsGranted_readPermissionRequired_customBasePermission() throws Exception{
		
		AclImpl acl = Mockito.mock(AclImpl.class);
		CumulativePermission cumulativePermission = new CumulativePermission().set(BasePermission.READ).set(BasePermission.WRITE);
		AccessControlEntryImpl acesSomasAdmin = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), cumulativePermission, true, false, false);
		List<AccessControlEntry> aces = new ArrayList<>();
		aces.add(acesSomasAdmin);

		Mockito.when(acl.getEntries()).thenReturn(aces);
		
		
		List<Permission> requiredPermissions = new ArrayList<>(); 
		requiredPermissions.add(BasePermission.READ);
		List<Sid> sids = new ArrayList<>(2);
		sids.add(new PrincipalSid("somas"));
		sids.add(new GrantedAuthoritySid("ROLE_ADMINISTRATOR"));
		
			
		boolean administrativeMode = false;
		
		boolean isGrantedActual = customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);
		
		Assert.assertTrue("somas with READ_WRITE privileges should be granted access", isGrantedActual);
	}
	
	
    @Test
    public void testIsGranted_writePermissionRequired() throws Exception {

        AclImpl acl = Mockito.mock(AclImpl.class);

        AccessControlEntryImpl acesSomasAdmin = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.WRITE, true, false, false);
        AccessControlEntryImpl acesUnauthorizedUser = new AccessControlEntryImpl(10, acl, new PrincipalSid("unauthorizedUser"), BasePermission.READ, true, false, false);
        List<AccessControlEntry> aces = new ArrayList<>();
        aces.add(acesSomasAdmin);
        aces.add(acesUnauthorizedUser);

        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<Permission> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(BasePermission.WRITE);
        List<Sid> sids = new ArrayList<>(2);
        sids.add(new PrincipalSid("somas"));
        sids.add(new GrantedAuthoritySid("ROLE_ADMINISTRATOR"));

        boolean administrativeMode = false;

        boolean isGrantedActual = customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);

        Assert.assertTrue("somas with WRITE privileges should be granted access", isGrantedActual);

        sids.clear();
        sids.add(new PrincipalSid("unauthorizedUser"));
        try {
            customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);
            Assert.fail("unauthorizedUser with READ privileges should NOT be granted access, exception should have been thrown ");
        } catch (NotFoundException e) {
            // success, ignore. NOTE: not annotated with expected exception as
            // we are doing two tests (see above)
        }

    }
	
    @Test
    public void testIsGranted_writePermissionRequired_userWithAdministrationPermission() throws Exception {

        AclImpl acl = Mockito.mock(AclImpl.class);

        AccessControlEntryImpl acesSomasAdmin = new AccessControlEntryImpl(9, acl, new PrincipalSid("somas"), BasePermission.ADMINISTRATION, true, false, false);
        AccessControlEntryImpl acesUnauthorizedUser = new AccessControlEntryImpl(10, acl, new PrincipalSid("unauthorizedUser"), BasePermission.WRITE, true, false, false);
        List<AccessControlEntry> aces = new ArrayList<>();
        aces.add(acesSomasAdmin);
        aces.add(acesUnauthorizedUser);

        Mockito.when(acl.getEntries()).thenReturn(aces);

        List<Permission> requiredPermissions = new ArrayList<>();
        requiredPermissions.add(BasePermission.WRITE);
        List<Sid> sids = new ArrayList<>(2);
        sids.add(new PrincipalSid("somas"));
        sids.add(new GrantedAuthoritySid("ROLE_ADMINISTRATOR"));

        boolean administrativeMode = false;

        boolean isGrantedActual = customPermissionGrantingStrategy.isGranted(acl, requiredPermissions, sids, administrativeMode);

        Assert.assertTrue("somas with Administration privileges should be granted access", isGrantedActual);
    }

}
