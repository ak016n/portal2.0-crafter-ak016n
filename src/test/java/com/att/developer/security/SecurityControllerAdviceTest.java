package com.att.developer.security;

import java.io.Serializable;
import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;

import com.att.developer.bean.Role;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;

public class SecurityControllerAdviceTest {

    private User actor = null;

    private static final String USER_NOT_PRIVILEGED_ID = "unprivilegedId";

    @Before
    public void before() {
        Role unprivilegedRole = new RoleBuilder().withName("notPrilegedRole").build();
        actor = new UserBuilder().withRole(unprivilegedRole).withId(USER_NOT_PRIVILEGED_ID).build();
    }

    
    @Test
    public void testCurrentUser_oauth2UserAuthentication() {

        Authentication userAuthentication = new UsernamePasswordAuthenticationToken(new SessionUser(actor), actor.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        
        OAuth2Request clientAuthentication = new OAuth2Request(Collections.emptyMap(), "someClientId", Collections.<GrantedAuthority>emptySet(), true, Collections.<String>emptySet(), Collections.<String>emptySet(), "redirectUri", Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap());
        OAuth2Authentication authentication = new OAuth2Authentication(clientAuthentication, userAuthentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = new SecurityControllerAdvice().currentUser(authentication);
        Assert.assertNotNull(sessionUser);
    }

    @Test
    public void testCurrentUser_oauth2ClientAuthentication() {

        OAuth2Request clientAuthentication = new OAuth2Request(Collections.emptyMap(), "someClientId", Collections.<GrantedAuthority>emptySet(), true, Collections.<String>emptySet(), Collections.<String>emptySet(), "redirectUri", Collections.<String>emptySet(), Collections.<String, Serializable>emptyMap());
        OAuth2Authentication authentication = new OAuth2Authentication(clientAuthentication, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = new SecurityControllerAdvice().currentUser(authentication);
        Assert.assertNull(sessionUser);
    }

    
    @Test
    public void testCurrentUser_nullAuthentication() {
        SessionUser sessionUser =  new SecurityControllerAdvice().currentUser(null);
        Assert.assertNull(sessionUser);
    }

    @Test
    public void testCurrentUser_sessionUserAuthentication() {

        Authentication authentication = new UsernamePasswordAuthenticationToken(new SessionUser(actor), actor.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = new SecurityControllerAdvice().currentUser(authentication);
        Assert.assertNotNull(sessionUser);
    }

    
    @Test
    public void testCurrentUser_prncipalStringAuthentication() {

        Authentication authentication = new UsernamePasswordAuthenticationToken("someStringPrincipal", "password", AuthorityUtils.createAuthorityList("BogusRole"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = new SecurityControllerAdvice().currentUser(authentication);
        Assert.assertNull(sessionUser);
    }
}
