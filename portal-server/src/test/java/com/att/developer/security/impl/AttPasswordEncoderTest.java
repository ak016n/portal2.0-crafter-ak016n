package com.att.developer.security.impl;

import org.junit.Assert;
import org.junit.Test;

import com.att.developer.security.impl.AttPasswordEncoder;

public class AttPasswordEncoderTest {

	AttPasswordEncoder attPasswordEncoder = new AttPasswordEncoder();
	CharSequence password = "password123";
	String encodedPassword = "{SSHA}Uexh4IbjBJU4l7DIq50itbMfFIMPEdoq+hYd4Q=="; //password123
	
	@Test
	public void testEncodeMatches() {
		String outputHash = attPasswordEncoder.encode(password);
		
		Assert.assertTrue(outputHash.startsWith("{SSHA}"));
	}

	@Test
	public void testMatches() {
		Assert.assertTrue(attPasswordEncoder.matches(password, encodedPassword));
	}
	
	@Test
	public void testMatches_wrongPassword() {
		Assert.assertFalse(attPasswordEncoder.matches("password", encodedPassword));
	}

}
