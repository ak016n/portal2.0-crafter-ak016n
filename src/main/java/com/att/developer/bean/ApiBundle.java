package com.att.developer.bean;

import java.time.Instant;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * Internally the Dates are still the old java.util.Date class.  We will not change this until JPA starts supporting the new java.time.Instant type.
 * @author so1234
 *
 */
@Entity
@Table(name = "api_bundle")
public class ApiBundle {
	
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
	

	public ApiBundle() {
	
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
	
	
	public String toString() {
		return new ToStringBuilder(this)
				.append("id", this.id)
				.append("endDate", this.endDate)
				.append("comments", this.comments)
				.append("startDate", this.startDate)
				.append("name", this.name)
				.append("lastUpdated", this.lastUpdated)
				.append("createdOn", this.createdOn)
				.toString();
	}

	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
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
		if(this.getId().equals(other.getId())){
			return true;
		}
		else{
			return false;
		}
	}

	
}
