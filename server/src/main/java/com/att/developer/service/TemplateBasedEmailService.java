package com.att.developer.service;

import java.util.Map;

public interface TemplateBasedEmailService {

	void sendMail(String templateName, Map<String, Object> messageContentMap);

}