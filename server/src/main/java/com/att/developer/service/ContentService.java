package com.att.developer.service;

import java.util.Map;

public interface ContentService {
	@SuppressWarnings("rawtypes")
	public Map getContent(String url, String login);
}
