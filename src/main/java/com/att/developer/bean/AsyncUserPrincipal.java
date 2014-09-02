package com.att.developer.bean;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.context.request.async.DeferredResult;

public class AsyncUserPrincipal {
	private static ConcurrentHashMap<String, DeferredResult<SessionUser>> asynUserPrincipalColl = new ConcurrentHashMap<>();
	
	public static void add(String key, DeferredResult<SessionUser> sessionUser) {
		asynUserPrincipalColl.put(key, sessionUser);
	}
	
	public static DeferredResult<SessionUser> get(String key) {
		return asynUserPrincipalColl.get(key);
	}
	
	public static void remove(String key) {
		asynUserPrincipalColl.remove(key);
	}
}
