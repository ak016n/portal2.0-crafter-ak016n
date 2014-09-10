package com.att.developer.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.AsyncUserPrincipalCache;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.wrapper.SessionUserWrapper;

@RestController
@RequestMapping("/uauth/user")
public class AsyncController {

	@RequestMapping(value = "/principal/async", method = RequestMethod.GET)
	public DeferredResult<SessionUserWrapper> userPrincipalAsync(@ModelAttribute SessionUser sessionUser) {
		DeferredResult<SessionUserWrapper> deferredResult = new DeferredResult<>();
		AsyncUserPrincipalCache.add(sessionUser.getUsername(), deferredResult);

		deferredResult.onTimeout(new Runnable() {
			public void run() {
				AsyncUserPrincipalCache.remove(sessionUser.getUsername());
			}
		});

		return deferredResult;
	}
	
	@RequestMapping(value = "/principal", method = RequestMethod.GET)
	public SessionUserWrapper userPrincipal(@ModelAttribute SessionUser sessionUser) {
		return new SessionUserWrapper(sessionUser);
	}

}
