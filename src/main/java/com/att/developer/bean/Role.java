package com.att.developer.bean;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

@Entity
@Table(name="role")
public class Role implements Serializable{
	
	
	private static final long serialVersionUID = 595161116378001109L;
	
	
	@Id
	private String id;
	private String name;
	private String description;

	
    // user level key roles
    public static final String ROLE_NAME_ORG_ADMIN = "OrganizationAdminAccess";
	
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


	@Override
	public int hashCode() {
		return Objects.hash(id);
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
		Role other = (Role) obj;
		return Objects.equals(this.id, other.id);
	}
	
	
}


