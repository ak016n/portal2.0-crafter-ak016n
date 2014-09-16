package com.att.developer.bean;

import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.security.acls.model.AccessControlEntry;


/**
 * Internally the Dates are still the old java.util.Date class.  We will not change this until JPA 
 * starts supporting the new java.time.Instant type.
 * 
 * @author so1234
 *
 */
@Entity
@Table(name = "api_bundle")
public class ApiBundle implements Serializable{
	

	private static final long serialVersionUID = 5819138290519388791L;

	@Id	
	private String id;
	
	private String name;

	@Column(name = "start_date")
	private Date startDate;

	@Column(name = "end_date")
	private Date endDate;

	private String comments;
	
	@Column(name = "created_on")
	private Date createdOn;
	
	@Column(name = "last_updated")
	private Date lastUpdated;

	@Transient
	private List<AccessControlEntry> accessControleEntries;

	public ApiBundle() {
	    this(UUID.randomUUID().toString());
	}
	
	public ApiBundle(String id){
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
		if(start != null){
			this.startDate = Date.from(start);
		}
		else{
			this.startDate = null;
		}
	}
	
	
	public Instant getEndDate() {
		return endDate != null ? endDate.toInstant() : null;
	}
	
	
	public void setEndDate(Instant end){
		if(end != null){
			this.endDate = Date.from(end);
		}
		else{
			this.endDate = null;
		}
	}


	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}


	public Instant getCreatedOn(){
		return this.createdOn != null ? createdOn.toInstant() : null;
	}

	

	public void setCreatedOn(Instant created){
		this.createdOn = Date.from(created);
	}

	
	public Instant getLastUpdated(){
		return this.lastUpdated != null ? lastUpdated.toInstant() : null;
	}
	
	
	public void setLastUpdated(Instant last){
		this.lastUpdated = Date.from(last);
	}
	
	/**
	 * Safe, ace instances not mutable.
	 * @return
	 */
	public List<AccessControlEntry> getAccessControleEntries() {
		return accessControleEntries;
	}
	
	
	public void setAccessControleEntries(List<AccessControlEntry> aces) {
		this.accessControleEntries = aces;
	}
	
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", this.id)
				.append("endDate", this.endDate)
				.append("comments", this.comments)
				.append("startDate", this.startDate)
				.append("name", this.name)
				.append("lastUpdated", this.lastUpdated)
				.append("createdOn", this.createdOn)
				.append("aces", this.accessControleEntries)
				.toString();
	}

	
	@Override
	public int hashCode() {
		return Objects.hash(this.id);
	}

	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ApiBundle other = (ApiBundle) obj;
		return Objects.equals(this.id, other.id);
	}

	
}
