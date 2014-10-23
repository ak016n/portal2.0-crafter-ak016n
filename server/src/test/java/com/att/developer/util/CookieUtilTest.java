package com.att.developer.util;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.security.DesCrypt;

public class CookieUtilTest {

	@InjectMocks
    private CookieUtil cookieUtil;
	
    @Mock
    private Cookie mockCookie;
    
    @Mock
    private DesCrypt desCrypt;
    
    private final static String COOKIE_VALUE = "testCookie";
    
    @Before
    public void initialize() {
    	MockitoAnnotations.initMocks(this);
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
        
        Mockito.when(desCrypt.encrypt("secret")).thenReturn("gibbrish");
        Mockito.when(desCrypt.decrypt("gibbrish")).thenReturn("secret");
        
        Cookie encryptedCookie = cookieUtil.getEncryptedCookie("encrypted", "secret", 100);
        String ddpUserCookieValue = cookieUtil.getDecryptedCookieValue(new Cookie[]{encryptedCookie}, "encrypted");
        
        Assert.assertEquals("secret", ddpUserCookieValue);
        
    }
    
    @Test
    public void testGetEncryptedCookie() {
        Mockito.when(desCrypt.encrypt("secret")).thenReturn("gibbrish");
        Cookie encryptedCookie = cookieUtil.getEncryptedCookie("encrypted", "secret", 100);
        Assert.assertTrue(encryptedCookie.getValue().equals("gibbrish"));
    }

}
