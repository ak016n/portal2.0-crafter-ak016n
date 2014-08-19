package com.att.developer.security;

import javax.annotation.Resource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.service.UserCreator;
import com.att.developer.service.UserService;


/**
 * makes the currentUser available for any controller in system (if they are logged in...)
 * @author so1234
 *
 */
@ControllerAdvice
public class SecurityControllerAdvice {
    
    private final Logger logger = LogManager.getLogger();
    
    @Resource
    private UserService userService;
    
    @Resource
    private UserCreator userCreator;
    
    @ModelAttribute
    public SessionUser currentUser(Authentication authentication) {
        
        logger.debug("Authentication is {}", authentication);
        if (authentication == null
            || authentication.getPrincipal() == null){
            logger.warn("no SessionUser, might be a problem!");
            return null;
        }
        Object principal = authentication.getPrincipal();

        if(authentication instanceof OAuth2Authentication){
            OAuth2Authentication oauth = (OAuth2Authentication) authentication;
            if(!oauth.isClientOnly()){
                String userId = (String)principal;
                logger.info("userId is {}", userId);
                User u = new User(); 
                u.setId(userId);
                User retrievedUser = userService.getUser(u);
                
                SessionUser sessionUser =  userCreator.buildSessionUserFromUserEntity(retrievedUser);
                //necessary to reset an OAuth2Authentication object with a Principal that is a sessionUser
                
                OAuth2Authentication revisedOauth = new OAuth2Authentication(oauth.getOAuth2Request(), new UsernamePasswordAuthenticationToken(sessionUser, "N/A", sessionUser.getAuthorities()));
                SecurityContextHolder.getContext().setAuthentication(revisedOauth);
                return sessionUser;
            }
            else{
                logger.info("We only have a Client ID as the principal, we don't need a session user");
                return null;
            }
        }
        else if(principal instanceof SessionUser){
            return (SessionUser)principal;
        }
        else{
            logger.warn("unrecognized Principal type, no SessionUser available, might be a problem!");
            return null;
        }
        
    }
}