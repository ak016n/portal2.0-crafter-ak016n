package com.att.developer.bean;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.wrapper.Principal;

public class AsyncUserPrincipalCache {
	private static ConcurrentHashMap<String, DeferredResult<Principal>> asynUserPrincipalColl = new ConcurrentHashMap<>();
	
	public static void add(String key, DeferredResult<Principal> sessionUser) {
		asynUserPrincipalColl.put(key, sessionUser);
	}
	
	public static DeferredResult<Principal> get(String key) {
		return asynUserPrincipalColl.get(key);
	}
	
	public static void remove(String key) {
		asynUserPrincipalColl.remove(key);
	}
}
