package com.att.developer.bean.builder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.att.developer.bean.api.ApiBundle;
import com.att.developer.bean.api.ApiWrapper;

public class ApiBundleBuilder {

	private ApiBundle apiBundle = new ApiBundle();

	public ApiBundleBuilder() {
		Instant now = Instant.now();
		apiBundle.setStartDate(now);
		apiBundle.setEndDate(now.plusSeconds(15000));
		apiBundle.setName("n_" + apiBundle.getId());
		apiBundle.setComments("some comments");
		Set<ApiWrapper> apiWrappers = new HashSet<ApiWrapper>();
		//apiWrappers.add(new ApiWrapperBuilder().build());
		apiBundle.setApiWrappers(apiWrappers);
		apiBundle.setCreatedOn(now);
		apiBundle.setLastUpdated(now);
	}

	public ApiBundle build() {
		return apiBundle;
	}

	public ApiBundleBuilder withId(String id) {
		this.apiBundle.setId(id);
		return this;
	}

	public ApiBundleBuilder withName(String name) {
		this.apiBundle.setName(name);
		return this;
	}
	
	public ApiBundleBuilder addApiWrapper(ApiWrapper apiWrapper) {
		this.apiBundle.getApiWrappers().add(apiWrapper);
		return this;
	}

}