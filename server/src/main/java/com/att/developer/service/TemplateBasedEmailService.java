package com.att.developer.service;

import java.util.Map;

import com.att.developer.exception.MessageProcessingException;

public interface TemplateBasedEmailService {

	void sendMail(String templateName, Map<String, Object> messageContentMap) throws MessageProcessingException;

}