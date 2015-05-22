package com.att.developer.controller;

import java.util.Locale;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.att.developer.service.impl.LocaleAwareResourceBundleMessageSource;

@Controller
@RequestMapping("/i18N")
public class I18NController {
	@Inject
	LocaleAwareResourceBundleMessageSource messageBundle;

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	public Properties getLocaleSpecificResourceBundle(@RequestParam String lang) {
		return messageBundle.getAllProperties(new Locale(lang));
	}
}
