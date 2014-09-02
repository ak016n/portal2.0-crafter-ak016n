package com.att.developer.controller;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.AsyncUserPrincipal;
import com.att.developer.bean.SessionUser;

@RestController
@RequestMapping("/uauth/async")
public class AsyncController {

	@RequestMapping(value="/principal", method = RequestMethod.GET)
	public DeferredResult<SessionUser> register(@ModelAttribute SessionUser sessionUser) {
		DeferredResult<SessionUser> futureSessionUser = new DeferredResult<>();
		AsyncUserPrincipal.add(sessionUser.getUsername(), futureSessionUser);
		return futureSessionUser;
	}
	
}
