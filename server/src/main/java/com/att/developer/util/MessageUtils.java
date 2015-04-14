package com.att.developer.util;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import com.att.developer.exception.MessageProcessingException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

@Component
public class MessageUtils {
    
	@Autowired
	private Configuration freeMarkerConfiguration;
	
	public void setFreeMarkerConfiguration(Configuration freeMarkerConfiguration) {
		this.freeMarkerConfiguration = freeMarkerConfiguration;
	}
	
	public String getMessage(String messageTemplateName, Map<String, Object> messageContentMap) throws MessageProcessingException {
		String message = null;
		try {
			message = FreeMarkerTemplateUtils.processTemplateIntoString(freeMarkerConfiguration.getTemplate(messageTemplateName), messageContentMap);
		} catch (IOException | TemplateException e) {
			throw new MessageProcessingException(e);
		}
		return message;
	}

}
