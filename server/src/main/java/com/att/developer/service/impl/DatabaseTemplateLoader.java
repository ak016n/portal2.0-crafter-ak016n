package com.att.developer.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.att.developer.service.GlobalScopedParamService;

import freemarker.cache.TemplateLoader;

public class DatabaseTemplateLoader implements TemplateLoader {

	@Resource
	private GlobalScopedParamService globalScopedParamService;
	
	@Override
	public Object findTemplateSource(String name) throws IOException {
		String[] templateName = StringUtils.split(name, "->");
		if(templateName.length < 3) {
			// templateSubpath = templateName[1];
			//Todo throw an exception
		} 
		Map<String, String> mapOfString = globalScopedParamService.getMap("MESSAGE", templateName[0].toUpperCase(), templateName[1]);
		return mapOfString.get(templateName[2]);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return -1;
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		return new StringReader((String) templateSource);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		//Do Nothing
	}

}
