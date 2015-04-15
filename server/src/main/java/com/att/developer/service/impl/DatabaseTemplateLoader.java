package com.att.developer.service.impl;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;

import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.util.Constants;

import freemarker.cache.TemplateLoader;

public class DatabaseTemplateLoader implements TemplateLoader {

	@Resource
	private GlobalScopedParamService globalScopedParamService;
	
	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}

	@Override
	public Object findTemplateSource(String name) throws IOException {
		String[] templateName = StringUtils.split(name, Constants.MESSAGE_SEPARATOR);
		if(templateName.length < 3) {
			throw new UnsupportedOperationException("Missing required field, expecting 3 level depth " + name);
		} 
		Map<String, String> mapOfString = globalScopedParamService.getMap(Constants.MESSAGE_TEMPLATE, templateName[0].toUpperCase(), templateName[1]);
		return mapOfString.get(templateName[2]);
	}

	@Override
	public long getLastModified(Object templateSource) {
		return -1;
	}

	@Override
	public Reader getReader(Object templateSource, String encoding) throws IOException {
		if(!(templateSource instanceof String)) {
			throw new UnsupportedOperationException("Expected templateSource to be of type String, instead got " + templateSource.getClass());
		}
		return new StringReader((String) templateSource);
	}

	@Override
	public void closeTemplateSource(Object templateSource) throws IOException {
		//Do Nothing
	}

}