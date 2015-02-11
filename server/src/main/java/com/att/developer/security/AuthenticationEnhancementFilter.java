package com.att.developer.security;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import com.att.developer.bean.SessionClient;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.service.UserCreator;
import com.att.developer.service.UserService;

/**
 * Needs to be last Filter in Security Filter Chain.
 * 
 * Takes care of making sure that a <code>SessionUser</code> gets put into the
 * SecurityContext Auth object (instead of just a String). <br/>
 * It also re-pulls the User from the UserService to make sure we have the very
 * latest information (primarily GrantedAuthorities). This is questionable to go
 * back to the database and defeats part of the point of using simple JWT tokens
 * to handle User information. At the very least we probably want to pull the
 * User from some sort of Cache.
 * 
 * 
 */
public class AuthenticationEnhancementFilter extends OncePerRequestFilter {

	private final Logger logger = LogManager.getLogger();

	@Resource
	private UserService userService;

	@Resource
	private UserCreator userCreator;
	
	@Autowired
	private ClientDetailsService clientDetailsService;

	public void setUserCreator(UserCreator creator) {
		this.userCreator = creator;
	}

	public void setUserService(UserService svc) {
		this.userService = svc;
	}
	
	public void setClientDetailsService(ClientDetailsService svc) {
		this.clientDetailsService = svc;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		logger.trace("doFilterInternal in progress..............................");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		buildUserAuth(authentication);
		filterChain.doFilter(request, response);
	}

	private void buildUserAuth(Authentication authentication) {
		logger.debug("auth is {}", authentication);

		if (authentication == null || authentication.getPrincipal() == null) {
			logger.warn("no Authentication or Principal, might be a problem!");
			return;
		}

		if (authentication instanceof OAuth2Authentication) {
			OAuth2Authentication oauth = (OAuth2Authentication) authentication;
			String userId = StringUtils.EMPTY;
			
			try {
				userId = (String) oauth.getPrincipal();
			} catch (ClassCastException e) {
					// hmmm, maybe it was already a SessionUser
					logger.info("ClassCastException occurred, probably is already a SessionUser");
					return;
			}
			
			if (!oauth.isClientOnly()) {
					logger.debug("userId is {}", userId);
					User u = new User();
					u.setId(userId);
					User retrievedUser = userService.getUser(u);

					SessionUser sessionUser = userCreator.buildSessionUserFromUserEntity(retrievedUser);
					// necessary to reset an OAuth2Authentication object with a
					// Principal that is a sessionUser

					OAuth2Authentication revisedOauth = new OAuth2Authentication(oauth.getOAuth2Request(),	new UsernamePasswordAuthenticationToken(sessionUser, "N/A",sessionUser.getAuthorities()));
					SecurityContextHolder.getContext().setAuthentication(revisedOauth);
					return;
			} else {
				ClientDetails clientDetails = clientDetailsService.loadClientByClientId(userId);
				SessionClient sessionClient = new SessionClient(clientDetails);
				OAuth2Authentication revisedOauth = new OAuth2AuthSessionClient(oauth.getOAuth2Request(), sessionClient);
				SecurityContextHolder.getContext().setAuthentication(revisedOauth);
				logger.info("We only have a Client ID as the principal, we don't need a session user");
				return;
			}
		} else {
			logger.info("Not an OAuth2Authentication, might be a pure Client Auth.");
			return;
		}
	}

}
