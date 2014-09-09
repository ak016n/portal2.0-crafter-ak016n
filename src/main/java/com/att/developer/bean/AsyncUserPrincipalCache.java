package com.att.developer.bean;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.web.context.request.async.DeferredResult;

import com.att.developer.bean.wrapper.SessionUserWrapper;

public class AsyncUserPrincipalCache {
	private static ConcurrentHashMap<String, DeferredResult<SessionUserWrapper>> asynUserPrincipalColl = new ConcurrentHashMap<>();
	
	public static void add(String key, DeferredResult<SessionUserWrapper> sessionUser) {
		asynUserPrincipalColl.put(key, sessionUser);
	}
	
	public static DeferredResult<SessionUserWrapper> get(String key) {
		return asynUserPrincipalColl.get(key);
	}
	
	public static void remove(String key) {
		asynUserPrincipalColl.remove(key);
	}
}
