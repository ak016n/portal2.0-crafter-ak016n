package com.att.developer.jms.consumer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.AsyncUserPrincipalCache;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.bean.wrapper.Principal;
import com.att.developer.bean.wrapper.UserJMSWrapper;
import com.att.developer.typelist.JMSEventType;

public class UserEventConsumer {

    private final Logger logger = LogManager.getLogger();
	
	public void handleMessage(UserJMSWrapper userJMSWrapper) {
			processMessage(userJMSWrapper.getJmsEventType(), userJMSWrapper.getUser());
	}

	private void processMessage(JMSEventType key, User user) {
		switch (key) {
			case UPDATE:
				logger.info("Update for user {}", user.getLogin());
				DeferredResult<Principal> deferredResult = AsyncUserPrincipalCache.get(user.getId());
				
				if(deferredResult != null) {
					Principal sessionUser = new Principal((SessionUser) SessionUser.buildSecurityUser(user));
					deferredResult.setResult(sessionUser);
				}
				
				break;
			default:
				break;
		}

	}

}
