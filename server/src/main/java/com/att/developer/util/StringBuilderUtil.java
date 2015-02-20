package com.att.developer.util;

public class StringBuilderUtil {

	public static String concatString(String... args) {
		StringBuilder builder = new StringBuilder();
		for(String each : args) {
			builder.append(each);
		}
		return builder.toString();
	}
	
	public static String concatStringWithDelimiter(String delimiter, String... args) {
		StringBuilder builder = new StringBuilder();
		boolean first = true;
		for(String each : args) {
			if(!first) {
				builder.append(delimiter);
			}
			builder.append(each);
			first = false;
		}
		return builder.toString();
	}
}
