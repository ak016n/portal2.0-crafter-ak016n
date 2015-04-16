package com.att.developer.security.impl;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.PBEParameterSpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DesCrypt {

    private static final int ITERATION_COUNT = 19;
	private static final String UTF_8_ENCODING = "UTF-8";
	private static final Logger logger = LoggerFactory.getLogger(DesCrypt.class);
    private Cipher encryptCipher;
    private Cipher decryptCipher;
    private String passPhrase = "UserID+Token!!";

    private static final byte[] salt = {-87, -101, -56, 50, 86, 53, -29, 3};

    public void setPassPhrase(String pPassPhrase) {
        this.passPhrase = pPassPhrase;
    }

    public void doStartService() {
        this.encryptCipher = getCipher(passPhrase, 1);
        this.decryptCipher = getCipher(passPhrase, 2);
    }

    public Cipher getCipher(String passPhrase, int mode) {
        Cipher cipher = null;
        try {
            KeySpec keySpec = new PBEKeySpec(passPhrase.toCharArray(), salt, ITERATION_COUNT);
            SecretKey key = SecretKeyFactory.getInstance("PBEWithMD5AndDES").generateSecret(keySpec);
            cipher = Cipher.getInstance(key.getAlgorithm());

            AlgorithmParameterSpec paramSpec = new PBEParameterSpec(salt, 19);

            cipher.init(mode, key, paramSpec);
        } catch (Exception e) {
            logger.error("DesCrypt.getCipher : Error : ", e);
        }
        return cipher;
    }

    public String encrypt(String str) {
        try {
            if (this.encryptCipher == null) {
                doStartService();
            }

            byte[] utf8 = str.getBytes("UTF8");
            byte[] enc = this.encryptCipher.doFinal(utf8);

            logger.debug("DesCrypter.encrypt : Encrypted Base64 Text : " + Base64.encodeBase64String(enc));

            String base64Encoded = Base64.encodeBase64String(enc);
            logger.debug("DesCrypt().encrypt(String) : String after second encoding : " + URLEncoder.encode(base64Encoded, UTF_8_ENCODING));

            return URLEncoder.encode(StringUtils.chomp(base64Encoded), UTF_8_ENCODING);
        } catch (Exception e) {
            logger.error("DesCrypter.encrypt : Error : ", e);
        }
        return null;
    }

    public String decrypt(String str) {
        try {
            if (this.decryptCipher == null) {
                doStartService();
            }
            if (logger.isDebugEnabled()) {
                logger.debug("String before decoding : " + str);
                logger.debug("\t length : " + str.length());
            }
            if (!(isStringStillEncoded(str))) {
                str = URLEncoder.encode(str.trim(), UTF_8_ENCODING);
                if (logger.isDebugEnabled()) {
                    logger.debug("No double encoding : Re-encoding the text : " + str);
                    logger.debug("\t length : " + str.length());
                }
            }
            String urlDecoded = URLDecoder.decode(str, UTF_8_ENCODING);

            byte[] dec = Base64.decodeBase64(urlDecoded);

            if (logger.isDebugEnabled()) {
                int counter = 0;
                while (dec.length > counter) {
                    logger.debug("String after decoding : " + (char) dec[counter]);
                    ++counter;
                }
            }

            byte[] utf8 = this.decryptCipher.doFinal(dec);

            if (logger.isDebugEnabled()) {
                logger.debug("DesCrypter.decrypt : Decrypted Text : " + new String(utf8, UTF_8_ENCODING));
            }

            return new String(utf8, "UTF8");
        } catch (Exception e) {
            logger.error("DesCrypter.decrypt : Error : ", e);
        }
        return null;
    }

    public String decryptBase64(String str) {
        String decodedStr = null;
        try {
            byte[] decoded = Base64.decodeBase64(str);
            decodedStr = new String(decoded, UTF_8_ENCODING);
        } catch (UnsupportedEncodingException e) {
            logger.error("DesCrypter.decryptBase64 : Error : ", e);
        }
        return decodedStr;
    }

    private boolean isStringStillEncoded(String str) {
        String[] specialChars = {"$", "&", "+", ",", "/", ":", ";", "=", "?", "@"};
        for (String each : specialChars) {
            if (str.contains(each)) {
                if (logger.isDebugEnabled()) {
                    logger.debug("isStringStillEncoded: false Character found : " + each);
                }
                return false;
            }
        }
        if (logger.isDebugEnabled()) {
            logger.debug("isStringStillEncoded: true");
        }
        return true;
    }
}
