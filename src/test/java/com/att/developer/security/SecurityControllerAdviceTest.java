package com.att.developer.security;

import org.junit.Assert;

import org.junit.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.att.developer.bean.SessionUser;

public class SecurityControllerAdviceTest {

    @Test
    public void testCurrentUser() {
        SecurityControllerAdvice advice = new SecurityControllerAdvice();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        SessionUser sessionUser = advice.currentUser(authentication);
        Assert.assertNull(sessionUser);
    }
}