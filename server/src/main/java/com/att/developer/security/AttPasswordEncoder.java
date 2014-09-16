package com.att.developer.security;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.att.developer.exception.PasswordEncoderException;

@Component("attPasswordEncoder")
public class AttPasswordEncoder implements PasswordEncoder {

    public static final String ALOGRITHM_SHA_1 = "SHA-1";
    public static final String SSHA_PREFIX = "{SSHA}";

    @Override
    public String encode(CharSequence rawPassword) {

        String password = rawPassword.toString();

        // Generate Random salt
        byte[] salt = new byte[8];
        new Random().nextBytes((byte[]) salt);

        try {
            MessageDigest sha = MessageDigest.getInstance("SHA");
            sha.update(password.getBytes());
            sha.update(salt);

            byte[] digest = sha.digest();
            byte[] all = new byte[digest.length + salt.length];
            System.arraycopy(digest, 0, all, 0, digest.length);
            System.arraycopy(salt, 0, all, digest.length, salt.length);

            return SSHA_PREFIX + new Base64().encodeToString(all);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordEncoderException(e);
        }
    }

    @Override
    public boolean matches(CharSequence rawPass, String encPass) {
        // Base64-decode the value of the encoded password string and break it
        // up into the SHA-1 digest and salt portions.
        
        if(StringUtils.isBlank(rawPass) || StringUtils.isBlank(encPass)) {
            throw new PasswordEncoderException("Values can't be null");
        }
        
        byte[] digestPlusSalt = Base64.decodeBase64(encPass.substring(6));
        byte[] saltBytes = new byte[8];
        byte[] digestBytes = new byte[digestPlusSalt.length - 8];

        System.arraycopy(digestPlusSalt, 0, digestBytes, 0, digestBytes.length);
        System.arraycopy(digestPlusSalt, digestBytes.length, saltBytes, 0, saltBytes.length);

        // Get a message digest that can generate SHA-1 hashes and use it to
        // digest the password plus the salt.
        MessageDigest sha1Digest = null;
        try {
            sha1Digest = MessageDigest.getInstance(ALOGRITHM_SHA_1);
        } catch (NoSuchAlgorithmException e) {
            throw new PasswordEncoderException(e);
        }

        sha1Digest.update(rawPass.toString().getBytes());
        sha1Digest.update(saltBytes);
        byte[] digest = sha1Digest.digest();
        return Arrays.equals(digestBytes, digest);
    }
}
