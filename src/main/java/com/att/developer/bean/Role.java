package com.att.developer.bean;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name="role")
public class Role {
	@Id
	private String id;
	private String name;
	private String description;

	public Role() {
		this.setId(java.util.UUID.randomUUID().toString());
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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String toString() {
		return new ToStringBuilder(this)
			.append("description", this.description).append("name", this.name)
			.append("id", this.id).toString();
	}
}


