package com.att.developer.service.impl;

import java.util.Locale;
import java.util.Properties;

import org.springframework.context.support.ReloadableResourceBundleMessageSource;

public class LocaleAwareResourceBundleMessageSource extends
		ReloadableResourceBundleMessageSource {

	public Properties getAllProperties(Locale locale) {
		clearCacheIncludingAncestors();
		PropertiesHolder propertiesHolder = getMergedProperties(locale);
		Properties properties = propertiesHolder.getProperties();

		return properties;
	}
}