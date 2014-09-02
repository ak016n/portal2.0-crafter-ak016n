package com.att.developer.jms.consumer;

import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.AsyncUserPrincipal;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;

public class UserEventConsumer {

    private final Logger logger = LogManager.getLogger();
	
	public void handleMessage(Map<String, User> userMap) {
		for (String key : userMap.keySet()) {
			processMessage(key, userMap.get(key));
		}
	}

	private void processMessage(String key, User user) {
		switch (key.toUpperCase()) {
			case "UPDATE":
				logger.info("Update for user {}", user.getLogin());
				DeferredResult<SessionUser> deferredResult = AsyncUserPrincipal.get(user.getLogin());
				deferredResult.setResult((SessionUser) SessionUser.buildSecurityUser(user));
				break;
			default:
				break;
		}

	}

}
