package com.att.developer.security;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.att.developer.bean.SessionUser;


/**
 * makes the currentUser available for any controller in system (if they are logged in...)
 * @author so1234
 *
 */
@ControllerAdvice
public class SecurityControllerAdvice {
 
    
    @ModelAttribute
    public SessionUser currentUser(Authentication authentication) {
        if (null == authentication || null == authentication.getPrincipal()){
            return null;
        }
        return (SessionUser)authentication.getPrincipal();
    }
}