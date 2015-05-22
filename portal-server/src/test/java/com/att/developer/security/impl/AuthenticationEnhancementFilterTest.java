package com.att.developer.security.impl;

import java.io.Serializable;
import java.util.Collections;

import javax.servlet.DispatcherType;
import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

import com.att.developer.bean.Role;
import com.att.developer.bean.SessionClient;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;
import com.att.developer.security.impl.AuthenticationEnhancementFilter;
import com.att.developer.service.UserCreator;
import com.att.developer.service.UserService;


public class AuthenticationEnhancementFilterTest {

    
    private User actor = null;
    
    private AuthenticationEnhancementFilter filter;
    @Mock
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @Mock
    private UserCreator mockUserCreator;
    
    @Mock
    private ClientDetailsService mockClientDetailsService;
    
    @Mock
    private UserService mockUserService;
    
    private static final String USER_NOT_PRIVILEGED_ID = "unprivilegedId";

    @Before
    public void before() {
        MockitoAnnotations.initMocks(this);

        Role unprivilegedRole = new RoleBuilder().withName("notPrilegedRole").build();
        actor = new UserBuilder().withRole(unprivilegedRole).withId(USER_NOT_PRIVILEGED_ID).build();
        
        filter = new AuthenticationEnhancementFilter();
        
        Mockito.when(request.getDispatcherType()).thenReturn(DispatcherType.REQUEST); // MOCK - Set basic request
        
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
        
        filter.setUserCreator(mockUserCreator);
        filter.setUserService(mockUserService);
        filter.setClientDetailsService(mockClientDetailsService);
        
    }

    
    @Test
    public void testDoFilter_happyPathUserIdInSecurityContext() throws Exception{
        
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(actor.getId(), actor.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        
        OAuth2Request clientAuthentication = new OAuth2Request(Collections.emptyMap(), "someClientId", Collections.<GrantedAuthority>emptySet(), true, Collections.<String>emptySet(), Collections.<String>emptySet(), "redirectUri", Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap());
        OAuth2Authentication authentication = new OAuth2Authentication(clientAuthentication, userAuthentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        Mockito.when(mockUserService.getUser(Mockito.isA(User.class))).thenReturn(actor);
        SessionUser sessionUser = new SessionUser(actor);
        Mockito.when(mockUserCreator.buildSessionUserFromUserEntity(Mockito.isA(User.class))).thenReturn(sessionUser);
        
        filter.doFilterInternal(request, response, filterChain);
        Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("Actual Auth is null", actualAuth);
        SessionUser actualSessionUser = (SessionUser)actualAuth.getPrincipal();
        Assert.assertNotNull("actualSessionUser is null", actualSessionUser);
        
        Mockito.verify(mockUserService, Mockito.times(1)).getUser(Mockito.any(User.class));
        Mockito.verify(mockUserCreator, Mockito.times(1)).buildSessionUserFromUserEntity(Mockito.any(User.class));
    }
    
    @Test
    public void testDoFilter_sessionUserAlreadyInSecurityContext() throws Exception{
        
        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(new SessionUser(actor), actor.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        
        OAuth2Request clientAuthentication = new OAuth2Request(Collections.emptyMap(), "someClientId", Collections.<GrantedAuthority>emptySet(), true, Collections.<String>emptySet(), Collections.<String>emptySet(), "redirectUri", Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap());
        OAuth2Authentication authentication = new OAuth2Authentication(clientAuthentication, userAuthentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        filter.doFilterInternal(request, response, filterChain);
        Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("Actual Auth is null", actualAuth);
        SessionUser sessionUser = (SessionUser)actualAuth.getPrincipal();
        Assert.assertNotNull("SessionUser is null", sessionUser);
    }

    
    @Test
    public void testDoFilter_clientUserOnly() throws Exception{
        
        Authentication userAuthentication = null;
        
        OAuth2Request clientAuthentication = new OAuth2Request(Collections.emptyMap(), "someClientId", Collections.<GrantedAuthority>emptySet(), true, Collections.<String>emptySet(), Collections.<String>emptySet(), "redirectUri", Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap());
        OAuth2Authentication authentication = new OAuth2Authentication(clientAuthentication, userAuthentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        Mockito.when(mockClientDetailsService.loadClientByClientId(Mockito.anyString())).thenReturn(new BaseClientDetails("X", "Y", "scope", "grant", "authorities"));
        
        filter.doFilterInternal(request, response, filterChain);
        Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();
        Assert.assertNotNull("Actual Auth is null", actualAuth);
        Assert.assertTrue(actualAuth.getPrincipal() instanceof SessionClient);
    }
    
    @Test 
    public void testDoFilter_nullAuthentication() throws Exception{
        SecurityContextHolder.getContext().setAuthentication(null);
        filter.doFilterInternal(request, response, filterChain);
        Authentication actualAuth = SecurityContextHolder.getContext().getAuthentication();
        
        Assert.assertNull("Acutal auth should be null", actualAuth);
    }

}
