package com.att.developer.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
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
    
    private final Logger logger = LogManager.getLogger();
    
    
    @ModelAttribute
    public SessionUser currentUser(Authentication authentication) {
        
        logger.debug("Authentication is {}", authentication);
        if (authentication == null
            || authentication.getPrincipal() == null){
            logger.warn("no Authentication or Principal, might be a problem!");
            return null;
        }
        Object principal = authentication.getPrincipal();

        if(authentication instanceof OAuth2Authentication){
            OAuth2Authentication oauth = (OAuth2Authentication) authentication;
            if(!oauth.isClientOnly()){
                SessionUser sessionUser = (SessionUser)principal;
                logger.debug("sessionUser is {}", sessionUser);
                return sessionUser;
            }
            else{
                logger.debug("We only have a Client ID as the principal, we don't need a session user");
                return null;
            }
        }
        else if(principal instanceof SessionUser){
            return (SessionUser)principal;
        }
        else{
            logger.info("No SessionUser available in Principal or Authentication type.");
            return null;
        }
        
    }
}