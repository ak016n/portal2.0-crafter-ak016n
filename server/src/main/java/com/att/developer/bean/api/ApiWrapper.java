package com.att.developer.bean.api;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name = "api_bundle_relationship")
public class ApiWrapper {
	
	@Id
	private String id;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="api_id", referencedColumnName = "id")
	private Api api;
	
	@ManyToOne(cascade={CascadeType.PERSIST, CascadeType.MERGE})
	@JoinColumn(name="api_bundle_id", referencedColumnName = "id")
	private ApiBundle apiBundle;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Api getApi() {
		return api;
	}

	public void setApi(Api api) {
		this.api = api;
	}

	public ApiBundle getApiBundle() {
		return apiBundle;
	}

	public void setApiBundle(ApiBundle apiBundle) {
		this.apiBundle = apiBundle;
	}

	public String toString() {
		return new ToStringBuilder(this).append("apiBundle", this.apiBundle)
				.append("id", this.id).append("api", this.api).toString();
	}

	
	
}
