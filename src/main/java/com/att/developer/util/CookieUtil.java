package com.att.developer.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.Cookie;

import org.springframework.stereotype.Component;

import com.att.developer.security.AESCrypt;

@Component
public class CookieUtil {

    private static final String UTF_8 = "UTF-8";

	public Cookie createSecureSessionCookie(Cookie dvcSessionCookie) {
        String secureCookieVal = "";
        if (dvcSessionCookie != null) {
            secureCookieVal = dvcSessionCookie.getValue();
        }
        secureCookieVal = secureCookieVal + System.currentTimeMillis() + Thread.currentThread().getId();
        String encryptValue = AESCrypt.encrypt(secureCookieVal);
        Cookie dvcSessionSecureId = null;
		try {
			dvcSessionSecureId = new Cookie(Constants.DEV_PORTAL_SESSION_SID, URLEncoder.encode(encryptValue, UTF_8));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
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
        	String decodedValue = null;
        	try {
				decodedValue =  URLDecoder.decode(ddpUserCookie.getValue(), UTF_8);
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
            ddpUserCookieValue = AESCrypt.decrypt(decodedValue);
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
