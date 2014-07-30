package com.att.developer.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
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
import com.att.developer.bean.EventLog;
import com.att.developer.bean.Organization;
import com.att.developer.bean.Role;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.dao.ApiBundleDAO;
import com.att.developer.security.PermissionManager;
import com.att.developer.service.EventTrackingService;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.typelist.EventType;

public class ApiBundleServiceImplTest {

    private ApiBundleServiceImpl apiBundleService;

    private static final String UNIQUE_BUNDLE_ID = "someUniqueId";

    @Mock
    private ApiBundleDAO mockApiBundleDAO;

    @Mock
    private PermissionManager mockPermissionManager;

    @Mock
    private EventTrackingService mockEventTrackingService;
    
    @Mock
    private GlobalScopedParamService mockGlobalScopedParamService;

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);
        apiBundleService = new ApiBundleServiceImpl();
        apiBundleService.setApiBundleDAO(mockApiBundleDAO);
        apiBundleService.setPermissionManager(mockPermissionManager);
        apiBundleService.setEventTrackingService(mockEventTrackingService);
        apiBundleService.setGlobalScopedParamService(mockGlobalScopedParamService);
        
        //make all checking Strict
        Mockito.when(mockGlobalScopedParamService.get("aclStrictParameterChecking")).thenReturn("true");
        
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
     * Mostly tests we are calling correct underlying methods and that
     * Administration permissions assigned.
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

    @Test(expected = IllegalArgumentException.class)
    public void testCreate_noUser() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        User actor = null;
        apiBundleService.create(initialBundle, actor);
    }

    @Test
    public void testEdit() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);

        apiBundleService.edit(initialBundle);
        Mockito.verify(mockApiBundleDAO, Mockito.times(1)).update(initialBundle);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
    }

    @Test
    public void testDelete() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        apiBundleService.delete(initialBundle);
        Mockito.verify(mockApiBundleDAO, Mockito.times(1)).delete(initialBundle);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
    }

    @Test
    public void testGrantPermission() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        Organization org = new OrganizationBuilder().build();
        User actor = new UserBuilder().build();
        Mockito.when(mockApiBundleDAO.load(initialBundle)).thenReturn(initialBundle);

        ArgumentCaptor<EventLog> argCaptor = ArgumentCaptor.forClass(EventLog.class);

        apiBundleService.grantPermission(initialBundle, org, actor);
        Mockito.verify(mockApiBundleDAO, Mockito.times(1)).load(initialBundle);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
        Mockito.verify(mockPermissionManager, Mockito.times(1)).grantPermissions(ApiBundle.class, UNIQUE_BUNDLE_ID, org, new CumulativePermission().set(BasePermission.WRITE).set(BasePermission.READ));
        Mockito.verifyNoMoreInteractions(mockPermissionManager);
        Mockito.verify(mockEventTrackingService, Mockito.times(1)).writeEvent(argCaptor.capture());
        Mockito.verifyNoMoreInteractions(mockEventTrackingService);
        Assert.assertEquals("event allowed should have been sent to EventTrackingService", EventType.API_BUNDLE_PERMISSION_UPDATED, argCaptor.getValue().getEventType());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrantPermission_noBundleFound() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        Organization org = new OrganizationBuilder().build();
        apiBundleService.grantPermission(initialBundle, org, null);
        Mockito.verify(mockApiBundleDAO, Mockito.times(1)).load(initialBundle);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGrantPermission_noActor() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        Organization org = new OrganizationBuilder().build();
        apiBundleService.grantPermission(initialBundle, org, null);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
    }

    @Test
    public void testRemoveAllPermissions() {
        ApiBundle initialBundle = new ApiBundle(UNIQUE_BUNDLE_ID);
        Organization org = new OrganizationBuilder().build();
        User actor = new UserBuilder().build();
        Mockito.when(mockApiBundleDAO.load(initialBundle)).thenReturn(initialBundle);

        ArgumentCaptor<EventLog> argCaptor = ArgumentCaptor.forClass(EventLog.class);

        apiBundleService.removeAllPermissions(initialBundle, org, actor);
        Mockito.verify(mockApiBundleDAO, Mockito.times(1)).load(initialBundle);
        Mockito.verifyNoMoreInteractions(mockApiBundleDAO);
        Mockito.verify(mockPermissionManager, Mockito.times(1)).removeAllPermissionForObjectForOrganization(ApiBundle.class, UNIQUE_BUNDLE_ID, org);
        Mockito.verifyNoMoreInteractions(mockPermissionManager);
        Mockito.verify(mockEventTrackingService, Mockito.times(1)).writeEvent(argCaptor.capture());
        Mockito.verifyNoMoreInteractions(mockEventTrackingService);
        Assert.assertEquals("event allowed should have been sent to EventTrackingService", EventType.API_BUNDLE_PERMISSION_UPDATED, argCaptor.getValue().getEventType());
    }

}