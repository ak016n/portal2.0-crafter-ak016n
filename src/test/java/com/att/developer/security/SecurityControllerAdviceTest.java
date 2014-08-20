package com.att.developer.security;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

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
        MockitoAnnotations.initMocks(this);

        Role unprivilegedRole = new RoleBuilder().withName("notPrilegedRole").build();
        actor = new UserBuilder().withRole(unprivilegedRole).withId(USER_NOT_PRIVILEGED_ID).build();
    }

    @Test
    public void testCurrentUser_nullAuthentication() {
        SecurityControllerAdvice advice = new SecurityControllerAdvice();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SessionUser sessionUser = advice.currentUser(authentication);
        Assert.assertNull(sessionUser);
    }

    @Test
    public void testCurrentUser_sessionUserAuthentication() {
        SecurityControllerAdvice advice = new SecurityControllerAdvice();

        Authentication authentication = new UsernamePasswordAuthenticationToken(new SessionUser(actor), actor.getPassword(), AuthorityUtils.createAuthorityList("BogusRole"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = advice.currentUser(authentication);
        Assert.assertNotNull(sessionUser);
    }

    
    @Test
    public void testCurrentUser_prncipalStringAuthentication() {
        SecurityControllerAdvice advice = new SecurityControllerAdvice();

        Authentication authentication = new UsernamePasswordAuthenticationToken("someStringPrincipal", "password", AuthorityUtils.createAuthorityList("BogusRole"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        SessionUser sessionUser = advice.currentUser(authentication);
        Assert.assertNull(sessionUser);
    }
}
