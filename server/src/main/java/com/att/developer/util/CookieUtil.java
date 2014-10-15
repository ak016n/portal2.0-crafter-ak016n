package com.att.developer.util;

import javax.servlet.http.Cookie;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.att.developer.security.DesCrypt;

@Component
public class CookieUtil {

    @Autowired
    private DesCrypt desCrypt;

    public void setDesCrypt(DesCrypt desCrypt) {
        this.desCrypt = desCrypt;
    }

    public Cookie removeCookie(Cookie cookie) {
        if (cookie != null) {
            cookie.setValue(null);
            cookie.setMaxAge(0);
            cookie.setPath("/");
        }
        return cookie;
    }

    public Cookie getCookie(Cookie[] cookies, String cookieName) {
        if (cookies == null) {
            return null;
        }
        for (int i = 0; i < cookies.length; ++i) {
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
            ddpUserCookieValue = this.desCrypt.decrypt(ddpUserCookie.getValue());
        }

        return ddpUserCookieValue;
    }

    public Cookie getEncryptedCookie(String cookieName, String cookieValue, int age) {
        String encryptedValue = this.desCrypt.encrypt(cookieValue);
        Cookie cookie = new Cookie(cookieName, encryptedValue);
        cookie.setMaxAge(age);
        cookie.setPath("/");
        return cookie;
    }
}