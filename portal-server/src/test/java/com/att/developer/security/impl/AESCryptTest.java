package com.att.developer.security.impl;

import org.apache.commons.codec.binary.Base64;
import org.junit.Assert;
import org.junit.Test;

import com.att.developer.security.impl.AESCrypt;

public class AESCryptTest {

	@Test
	public void testEncryptDecrypt() {
		
		String encryptedString = null;
		String outputOfDecryption = null;
		String stringToBeEncrypted = "Hello World";
		
		try {
			encryptedString = AESCrypt.encrypt(stringToBeEncrypted);
			outputOfDecryption = AESCrypt.decrypt(encryptedString);
		} catch (SecurityException e) {
			 if(e.getMessage().equals("java.security.InvalidKeyException: Illegal key size or default parameters")) {
				 Assert.fail("Missing JCE 8 file - http://www.oracle.com/technetwork/java/javase/downloads/jce8-download-2133166.html");	 
			 }
			Assert.fail();
		}
		
		Assert.assertTrue(Base64.isBase64(encryptedString.getBytes()));
		Assert.assertEquals(stringToBeEncrypted, outputOfDecryption);
	}
}
