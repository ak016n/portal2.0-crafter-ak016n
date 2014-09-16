package com.att.developer.util;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import com.att.developer.security.AESCrypt;

@Component
public class CookieUtil {

    public Cookie createSecureSessionCookie(Cookie dvcSessionCookie) {
        String secureCookieVal = "";
        if (dvcSessionCookie != null) {
            secureCookieVal = dvcSessionCookie.getValue();
        }
        secureCookieVal = secureCookieVal + System.currentTimeMillis() + Thread.currentThread().getId();
        Cookie dvcSessionSecureId = new Cookie(Constants.DEV_PORTAL_SESSION_SID, AESCrypt.encrypt(secureCookieVal));
        dvcSessionSecureId.setSecure(true);
        dvcSessionSecureId.setPath("/");
        return dvcSessionSecureId;
    }
    
    public Cookie removeCookie(Cookie cookie) {
        if(cookie != null){
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
        }
        return cookie;
    }
    
    /**
     * Retrieves a Cookie based on the cookie name
     */
    public Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies == null) {
            return null;
        }
        for (int i = 0; i < cookies.length; i++) {
            Cookie cookie = cookies[i];
            if (cookieName.equalsIgnoreCase(cookie.getName())) {
                return cookie;
            }
        }
        return null;
    }
    
    public String getDecryptedCookieValue(Cookie[] cookies, String cookieName) {
        Cookie ddpUserCookie = getCookie(cookies, cookieName);
        String ddpUserCookieValue = null;
        if (ddpUserCookie != null) {
            ddpUserCookieValue = AESCrypt.decrypt(ddpUserCookie.getValue());
        }
        
        return ddpUserCookieValue;
    }
    
    public Cookie getEncryptedCookie(String cookieName, String cookieValue, int age) {
        String encryptedValue = AESCrypt.encrypt(cookieValue);
        Cookie cookie = new Cookie(cookieName, encryptedValue);
        cookie.setMaxAge(age);
        cookie.setPath("/");
        return cookie;
    }
    
}
