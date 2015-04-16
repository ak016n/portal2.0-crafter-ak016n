package com.att.developer.security.impl;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AESCrypt {
	
	
	private static final Logger logger = LogManager.getLogger();
	
	private static byte[] key = { 0x54, 0x68, 0x65, 0x47, 0x6F, 0x6F, 0x64, 0x2C, 0x54, 0x68, 0x65, 0x42, 0x61, 0x64, 0x41, 0x6E, 0x64, 0x54, 0x68, 0x65, 0x55, 0x67, 0x6C, 0x79 };
	
	public static String encrypt(String strToEncrypt) {
		String encryptedString = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			encryptedString = Base64.encodeBase64String(cipher.doFinal(strToEncrypt.getBytes()));
		} catch (Exception e) {
			logger.error("Unable to encrypt string : " + strToEncrypt, e);
			throw new SecurityException(e);
		}
		return encryptedString;
	}

	public static String decrypt(String strToDecrypt) {
		String decryptedString = null;
		try {
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING");
			final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			decryptedString = new String(cipher.doFinal(Base64.decodeBase64(strToDecrypt)));
		} catch (Exception e) {
			logger.error("Unable to decrypt string : " + decryptedString, e);
			throw new SecurityException(e);
		}
		return decryptedString;
	}

}
