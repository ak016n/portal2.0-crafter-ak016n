package com.att.developer.bean.api;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.bean.PermissionModel;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;

/**
 * Internally the Dates are still the old java.util.Date class. We will not
 * change this until JPA starts supporting the new java.time.Instant type.
 * 
 */
@Entity
@Table(name = "api_bundle")
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property="id")
public class ApiBundle implements Serializable {

	private static final long serialVersionUID = 5819138290519388791L;

	@Id
	private String id;

	private String name;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	private String comments;
	
	@Transient
	private PermissionModel permission;

	@OneToMany(cascade={CascadeType.PERSIST, CascadeType.MERGE}, mappedBy = "apiBundle")
	private Set<ApiWrapper> apiWrappers;

	@Column(name = "created_on", insertable = false, updatable = false)
	private Date createdOn;

	@Column(name = "last_updated", insertable = false)
	private Date lastUpdated;

	public ApiBundle() {
		this(UUID.randomUUID().toString());
	}

	public ApiBundle(String id) {
		this.setId(id);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Instant getStartDate() {
		return startDate != null ? startDate.toInstant() : null;
	}

	public void setStartDate(Instant start) {
		if (start != null) {
			this.startDate = Date.from(start);
		} else {
			this.startDate = null;
		}
	}

	public Instant getEndDate() {
		return endDate != null ? endDate.toInstant() : null;
	}

	public void setEndDate(Instant end) {
		if (end != null) {
			this.endDate = Date.from(end);
		} else {
			this.endDate = null;
		}
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public Set<ApiWrapper> getApiWrappers() {
		return apiWrappers;
	}

	public void setApiWrappers(Set<ApiWrapper> apiWrappers) {
		this.apiWrappers = apiWrappers;
	}
	
	public PermissionModel getPermission() {
		return permission;
	}

	public void setPermission(PermissionModel permission) {
		this.permission = permission;
	}

	public Instant getCreatedOn() {
		return this.createdOn != null ? createdOn.toInstant() : null;
	}

	public void setCreatedOn(Instant created) {
		this.createdOn = Date.from(created);
	}

	public Instant getLastUpdated() {
		return this.lastUpdated != null ? lastUpdated.toInstant() : null;
	}

	public void setLastUpdated(Instant last) {
		this.lastUpdated = Date.from(last);
	}

	public String toString() {
		return new ToStringBuilder(this).append("id", this.id)
				.append("endDate", this.endDate)
				.append("comments", this.comments)
				.append("startDate", this.startDate).append("name", this.name)
				.append("lastUpdated", this.lastUpdated)
				.append("createdOn", this.createdOn).toString();
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		ApiBundle other = (ApiBundle) obj;
		return Objects.equals(this.id, other.id);
	}

}
