package com.att.developer.service.impl;

import java.util.List;

import javax.annotation.Resource;
import javax.transaction.Transactional;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.acls.domain.BasePermission;
import org.springframework.security.acls.domain.PrincipalSid;
import org.springframework.security.acls.model.AccessControlEntry;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.test.context.transaction.TransactionConfiguration;

import com.att.developer.bean.ApiBundle;
import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.ApiBundleBuilder;
import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.config.IntegrationContext;
import com.att.developer.config.IntegrationSecurityContext;
import com.att.developer.service.ApiBundleService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={IntegrationContext.class, IntegrationSecurityContext.class}, loader=AnnotationConfigContextLoader.class)
@TransactionConfiguration(transactionManager="txManager", defaultRollback = true)
@Transactional
public class ApiBundleServiceIntegrationTest {

    
    //under test
    @Resource
    private ApiBundleService apiBundleService;
    
    
    private ApiBundle apiBundle;
    
    private User actorAdmin = null;
    private User actorUnprivileged = null;
    private User userApiBundleOrgMember = null;
    private User userNoBundlesOrgMember = null;
    
    private Organization orgWithApiBundles = null;
    private Organization orgNoApiBundles = null;
    
    private static final String USER_ADMIN_ID = "adminId";
    private static final String USER_NOT_PRIVILEGED_ID = "unprivilegedId";
           
    private static final String ORG_WITH_API_BUNDLES_ID = "orgWithApiBundlesId";
    private static final String ORG_NO_API_BUNDLES_ID = "orgNoApiBundlesId";
    
    
    @Before
    public void before(){
        MockitoAnnotations.initMocks(this);
        
        apiBundle = new ApiBundleBuilder().build();
        
        Role adminRole = new RoleBuilder().withName(Role.ROLE_NAME_SYS_ADMIN).build();
        actorAdmin = new UserBuilder().withRole(adminRole).withId(USER_ADMIN_ID).build();
        
        Role unprivilegedRole = new RoleBuilder().withName("notPrilegedRole").build();
        actorUnprivileged = new UserBuilder().withRole(unprivilegedRole).withId(USER_NOT_PRIVILEGED_ID).build();
    
        userApiBundleOrgMember = new UserBuilder().build();
        userNoBundlesOrgMember = new UserBuilder().build();

        orgWithApiBundles = new OrganizationBuilder()
                                .withId(ORG_WITH_API_BUNDLES_ID)
                                .withName("hasBundles")
                                .withUser(userApiBundleOrgMember)
                                .build();
        orgNoApiBundles = new OrganizationBuilder()
                                .withId(ORG_NO_API_BUNDLES_ID)
                                .withName("noBundles")
                                .withUser(userNoBundlesOrgMember)
                                .build();
        
    }

    
    @Test(expected=AccessDeniedException.class) 
    public void testCreate_notAdminRole(){
        Authentication authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(actorUnprivileged), actorUnprivileged.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        //need to create a new bundle for failure case, otherwise we get a database constraint collision.
        apiBundleService.create(new ApiBundleBuilder().build(), actorUnprivileged);
    }
    
    
    @Test
    public void testCreate() {
        Authentication authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(actorAdmin), actorAdmin.getPassword(), AuthorityUtils.createAuthorityList(Role.ROLE_NAME_SYS_ADMIN));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        
        //CREATE
        apiBundleService.create(apiBundle, actorAdmin);
        
        //RETRIEVE and verify CREATE
        ApiBundle afterCreateBundle = apiBundleService.getApiBundle(apiBundle.getId());
        Assert.assertNotNull(afterCreateBundle);
        Assert.assertEquals(apiBundle.getName(), afterCreateBundle.getName());
        Assert.assertEquals(apiBundle.getId(), afterCreateBundle.getId());
        List<AccessControlEntry> acesAfterCreate = afterCreateBundle.getAccessControleEntries();
        Assert.assertEquals("aces should have one entry", 1, acesAfterCreate.size());
        AccessControlEntry aceAdmin = acesAfterCreate.get(0);
        Assert.assertEquals("wrong permission", BasePermission.ADMINISTRATION, aceAdmin.getPermission());
        Assert.assertEquals("wrong principal", new PrincipalSid("adminId"), aceAdmin.getSid());
        
        //GRANT PERMISSIONS
        apiBundleService.grantPermission(apiBundle, orgWithApiBundles, actorAdmin);
        ApiBundle afterGrantBundle = apiBundleService.getApiBundle(apiBundle.getId());
        Assert.assertNotNull(afterGrantBundle);
        Assert.assertEquals("aces should have two entries", 2, afterGrantBundle.getAccessControleEntries().size());
        
        //Retrieve ALL bundles as Admin
        List<ApiBundle> allBundles = apiBundleService.getAll();
        Assert.assertTrue("missing bundle", allBundles.contains(afterGrantBundle));
        
        //Retrieve all Bundles as userNoBundlesOrgMember user
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(userNoBundlesOrgMember), userNoBundlesOrgMember.getPassword(), AuthorityUtils.createAuthorityList(orgNoApiBundles.getId()));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        List<ApiBundle> allBundlesForNoBundlesOrg = apiBundleService.getAll();
        Assert.assertFalse("contains bundle for which we should have no privilege", allBundlesForNoBundlesOrg.contains(afterGrantBundle));
        
        //Retrieve all Bundles as userApiBundleOrgMember
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(userApiBundleOrgMember), userApiBundleOrgMember.getPassword(), AuthorityUtils.createAuthorityList(orgWithApiBundles.getId()));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        List<ApiBundle> allBundlesForOrgWithApiBundles = apiBundleService.getAll();
        Assert.assertTrue("does not contain bundle for which we have a privilege", allBundlesForOrgWithApiBundles.contains(afterGrantBundle));
        

        //REMOVE permissions for Organization
        //reset to Admin user
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(actorAdmin), actorAdmin.getPassword(), AuthorityUtils.createAuthorityList(Role.ROLE_NAME_SYS_ADMIN));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        
        apiBundleService.removeAllPermissions(afterGrantBundle, orgWithApiBundles, actorAdmin);
        
        ApiBundle afterRemovePermissionsBundle = apiBundleService.getApiBundle(afterGrantBundle.getId());
        Assert.assertNotNull(afterRemovePermissionsBundle);
        
        //Retrieve ALL bundles as Admin
        List<ApiBundle> afterRemoveAllBundlesForAdmin = apiBundleService.getAll();
        Assert.assertTrue("missing bundle", afterRemoveAllBundlesForAdmin.contains(afterRemovePermissionsBundle));


        //Retrieve all Bundles as userNoBundlesOrgMember user
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(userNoBundlesOrgMember), userNoBundlesOrgMember.getPassword(), AuthorityUtils.createAuthorityList(orgNoApiBundles.getId()));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        List<ApiBundle> afterRemoveAllBundlesForNoBundlesOrg = apiBundleService.getAll();
        Assert.assertFalse("contains bundle for which we should have no privilege", afterRemoveAllBundlesForNoBundlesOrg.contains(afterRemovePermissionsBundle));
        
        //Retrieve all Bundles as userApiBundleOrgMember
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(userApiBundleOrgMember), userApiBundleOrgMember.getPassword(), AuthorityUtils.createAuthorityList(orgWithApiBundles.getId()));
        SecurityContextHolder.getContext().setAuthentication(authRequest);
        List<ApiBundle> afterRemoveAllBundlesForOrgWithApiBundles = apiBundleService.getAll();
        Assert.assertFalse("contains bundle for which we should NOT have a privilege after the removal of this org's permissions", afterRemoveAllBundlesForOrgWithApiBundles.contains(afterRemovePermissionsBundle));
        
        //TODO: add EDIT ApiBundle Test
        
        //DELETE ApiBundle
        //try to Delete as normal member
        try{
            apiBundleService.delete(afterRemovePermissionsBundle);
            Assert.fail("should not be able to DELETE an ApiBundle as a non-privileged user") ;
        }
        catch(AccessDeniedException e){
            //success to see this, ignore
        }
        
        //reset to Admin user
        authRequest = new UsernamePasswordAuthenticationToken(new SessionUser(actorAdmin), actorAdmin.getPassword(), AuthorityUtils.createAuthorityList(Role.ROLE_NAME_SYS_ADMIN));
        SecurityContextHolder.getContext().setAuthentication(authRequest);

        apiBundleService.delete(afterRemovePermissionsBundle);
        
        ApiBundle afterDeleteBundle = apiBundleService.getApiBundle(apiBundle.getId());
        
        Assert.assertNull("bundle should NOT be found after deletion", afterDeleteBundle);
    }
    

}
