package com.att.developer.service;

import java.util.Map;

public interface TemplateBasedEmailService {

	public abstract void sendMail(String templateName,
			Map<String, Object> messageContentMap);

}