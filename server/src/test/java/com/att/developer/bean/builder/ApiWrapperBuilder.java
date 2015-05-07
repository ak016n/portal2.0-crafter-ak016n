package com.att.developer.bean.builder;

import com.att.developer.bean.api.Api;
import com.att.developer.bean.api.ApiBundle;
import com.att.developer.bean.api.ApiWrapper;

public class ApiWrapperBuilder {

	private ApiWrapper apiWrapper = new ApiWrapper();

	public ApiWrapperBuilder() {
		apiWrapper.setId(java.util.UUID.randomUUID().toString());
		apiWrapper.setApiBundle(new ApiBundleBuilder().build());
		apiWrapper.setApi(new ApiBuilder().build());
	}

	public ApiWrapper build() {
		return apiWrapper;
	}
	
	public ApiWrapperBuilder withApi(Api api) {
		apiWrapper.setApi(api);
		return this;
	}
	
	public ApiWrapperBuilder withApiBundle(ApiBundle apiBundle) {
		apiWrapper.setApiBundle(apiBundle);
		return this;
	}

	public ApiWrapperBuilder withId(String id) {
		this.apiWrapper.setId(id);
		return this;
	}

}