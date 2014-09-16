package com.att.developer.controller;

import java.io.IOException;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.att.developer.service.LoginSecurityService;
import com.att.developer.util.Constants;
import com.att.developer.util.CookieUtil;

@Controller
@RequestMapping("/auth")
public class LoginController {
	
    private static final String STRING_TRUE = "true";
    private static final String ATTR_KEY_ERROR = "error";
    public static final String DEST_PAGE = "destPage";
    public static final String LOGIN_PAGE_URL = "jsp/auth/login.jsp";
    public static final String HOME_PAGE_URL = "/developer";

    @Autowired
    private LoginSecurityService loginSecurityService;
    
    @Autowired
    private CookieUtil cookieUtil;
    
    public void setLoginSecurityService(LoginSecurityService loginSecurityService) {
        this.loginSecurityService = loginSecurityService;
    }

    public void setCookieUtil(CookieUtil cookieUtil) {
    	this.cookieUtil = cookieUtil;
    }
    
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(HttpServletRequest request, HttpServletResponse response) {
        String destPage = request.getParameter(DEST_PAGE);
        if (!StringUtils.isEmpty(destPage)) {
            destPage = destPage + "&t=" + request.getParameter("t");
        }
        request.getSession().setAttribute(DEST_PAGE, destPage);
        
        return LOGIN_PAGE_URL;
    }
    
    @RequestMapping(value = "/loginfailed", method = RequestMethod.GET)
    public String loginError(HttpServletRequest request, HttpServletResponse response, ModelMap model) {
        model.addAttribute(ATTR_KEY_ERROR, STRING_TRUE);
        return LOGIN_PAGE_URL;
    }
    
    @RequestMapping(value = "/loginsuccess", method = RequestMethod.GET)
    public void loginSuccess(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws IOException {

        // Backward compatibility with ATG
        
        // Add secure session cookie to response
    	secureSessionCookie(request, response);
    	
        // Add GA related tracking cookie
        
        // Value stored using filter
        
        // Redirect them to where they came from or just sent them to home page
        String destPage = (String) request.getSession().getAttribute(DEST_PAGE);
        
        if(StringUtils.isBlank(destPage)) {
            destPage = HOME_PAGE_URL;
        }
        
        response.sendRedirect(destPage);
    }
    
    private void secureSessionCookie(HttpServletRequest request, HttpServletResponse response) {
        Cookie dvcSessionCookie = cookieUtil.getCookie(request.getCookies(), Constants.DEV_PORTAL_SESSION_SID);
        Cookie dvcSessionSecureCookie = cookieUtil.createSecureSessionCookie(dvcSessionCookie);
        request.getSession().setAttribute(Constants.DEV_PORTAL_SESSION_SID, dvcSessionSecureCookie.getValue());
        response.addCookie(dvcSessionSecureCookie);
    }
}
