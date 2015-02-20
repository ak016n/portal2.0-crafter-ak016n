package com.att.developer.util;



import org.junit.Assert;
import org.junit.Test;

public class StringBuilderUtilTest {

	@Test
	public void testConcatString() {
		Assert.assertEquals("AB", StringBuilderUtil.concatString("A", "B"));
		Assert.assertEquals("ABCD", StringBuilderUtil.concatString("A", "B", "C", "D"));
	}

	@Test
	public void testConcatStringWithDelimiter() {
		Assert.assertEquals("A,B", StringBuilderUtil.concatStringWithDelimiter(",", "A", "B"));
		Assert.assertEquals("A B C D", StringBuilderUtil.concatStringWithDelimiter(" ", "A", "B", "C", "D"));
	}
}
