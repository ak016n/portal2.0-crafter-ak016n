package com.att.developer.bean.api;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

@Entity
@Table(name = "api_bundle_relationship")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class ApiWrapper {
	
	@Id
	private String id;
	
	@ManyToOne
	@JoinColumn(name="api_id", referencedColumnName = "id")
	private Api api;
	
	@ManyToOne
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

/*	public boolean equals(Object object) {
		if (!(object instanceof ApiWrapper)) {
			return false;
		}
		ApiWrapper rhs = (ApiWrapper) object;
		return new EqualsBuilder()
				.append(this.apiBundle.getName(), rhs.apiBundle.getName())
				.append(this.api.getName(), rhs.api.getName()).isEquals();
	}

	public int hashCode() {
		return new HashCodeBuilder(-514759121, 1774156527)
				.append(this.apiBundle.getName()).append(this.api.getName())
				.toHashCode();
	}
*/
	
	
}
