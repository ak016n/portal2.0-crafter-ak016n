package com.att.developer.exception;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptorAdapter;

public class TimeoutDeferredResultProcessingInterceptor extends DeferredResultProcessingInterceptorAdapter {
	@Override
	public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) throws Exception {
		HttpServletResponse servletResponse = request.getNativeResponse(HttpServletResponse.class);
		HttpServletRequest servletRequest = request.getNativeRequest(HttpServletRequest.class);
		
		if (servletRequest instanceof HttpServletRequest && !servletResponse.isCommitted()) {
			servletRequest.getAsyncContext().complete();
			servletResponse.sendError(HttpStatus.GATEWAY_TIMEOUT.value());
		}
		return false;
	}
}