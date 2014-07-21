package com.att.developer.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.authentication.event.AuthenticationFailureServiceExceptionEvent;
import org.springframework.stereotype.Component;

import com.att.developer.service.impl.LoginSecurityService;

@Component
public class LoginFailureEventListener implements ApplicationListener<AbstractAuthenticationFailureEvent>  {

    @Autowired
    private LoginSecurityService loginSecurityService;
    
    public void setLoginSecurityService(LoginSecurityService loginSecurityService) {
        this.loginSecurityService = loginSecurityService;
    }
    
    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent event) {
        
        if(event instanceof AuthenticationFailureBadCredentialsEvent || event instanceof AuthenticationFailureLockedEvent || event instanceof AuthenticationFailureServiceExceptionEvent) {
            String userName = (String) event.getAuthentication().getPrincipal();
            
            if(StringUtils.isNotBlank(userName)) {
                //loginSecurityService.createUserLogEvent(userName);
                loginSecurityService.addSecurityDelay();
            }    
        }

    }


}
