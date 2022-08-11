package com.net128.lib.spring.jpa.csv.util;

public class NameUtil {
	public static String camel2Snake(String str) {
		return str.replaceAll("([A-Z][a-z])", "_$1")
				.replaceAll("^_", "").toUpperCase();
	}
}
