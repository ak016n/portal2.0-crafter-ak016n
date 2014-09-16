package com.att.developer.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.AsyncUserPrincipalCache;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.wrapper.Principal;

@RestController
@RequestMapping("/uauth/user/principal")
public class AsyncController {

	@RequestMapping(value = "/async", method = RequestMethod.GET)
	public DeferredResult<Principal> userPrincipalAsync(@ModelAttribute SessionUser sessionUser) {
		DeferredResult<Principal> deferredResult = new DeferredResult<>();
		AsyncUserPrincipalCache.add(sessionUser.getUsername(), deferredResult);

		deferredResult.onTimeout(new Runnable() {
			public void run() {
				AsyncUserPrincipalCache.remove(sessionUser.getUsername());
			}
		});

		return deferredResult;
	}
	
	@RequestMapping(method = RequestMethod.GET)
	public Principal userPrincipal(@ModelAttribute SessionUser sessionUser) {
		return new Principal(sessionUser);
	}

}
