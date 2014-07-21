package com.att.developer.util;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class CookieUtilTest {

    private CookieUtil cookieUtil;
    private Cookie mockCookie;
    private final static String COOKIE_VALUE = "testCookie";
    
    @Before
    public void initialize() {
        cookieUtil = new CookieUtil();
        mockCookie = Mockito.mock(Cookie.class);
    }
    
    @Test
    public void testCreateSecureSessionCookie_happyPath() {
        Mockito.when(mockCookie.getValue()).thenReturn(COOKIE_VALUE);
        Cookie returnCookie = cookieUtil.createSecureSessionCookie(mockCookie);
        Assert.assertTrue(returnCookie.getSecure());
        Assert.assertEquals("/", returnCookie.getPath());
        Assert.assertNotNull(returnCookie);
    }
    
    @Test
    public void testRemoveCookie_happyPath() {        
        Cookie returnCookie = cookieUtil.removeCookie(new Cookie(COOKIE_VALUE, COOKIE_VALUE));
        Assert.assertEquals("/", returnCookie.getPath());
        Assert.assertEquals(0, returnCookie.getMaxAge());
        Assert.assertNull(returnCookie.getValue());
    }
   
    @Test
    public void testGetCookie_happyPath() {      
        Cookie[] cookieArray = new Cookie[1];
        cookieArray[0] = new Cookie(COOKIE_VALUE,COOKIE_VALUE);
        Cookie returnCookie = cookieUtil.getCookie(cookieArray, COOKIE_VALUE);        
        Assert.assertNotNull(returnCookie);
    }
    
    @Test
    public void testGetCookie_NullCookieScenario() {
        Cookie returnCookie = cookieUtil.getCookie(null, COOKIE_VALUE);        
        Assert.assertNull(returnCookie);
    }
    
    @Test
    public void testGetDecryptedCookieValue() {
        Cookie encryptedCookie = cookieUtil.getEncryptedCookie("encrypted", "secret", 100);
        String ddpUserCookieValue = cookieUtil.getDecryptedCookieValue(new Cookie[]{encryptedCookie}, "encrypted");
        
        Assert.assertEquals("secret", ddpUserCookieValue);
        
    }
    
    @Test
    public void testGetEncryptedCookie() {
        Cookie encryptedCookie = cookieUtil.getEncryptedCookie("encrypted", "secret", 100);
        Assert.assertFalse(encryptedCookie.getValue().equals("secret"));
    }

}
