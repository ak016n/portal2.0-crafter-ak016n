package com.att.developer.bean;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.OrganizationType;

@Entity
@Table(name="state")
public class OrganizationState {
	@Id
	private String id;
	
	@Column(name="state_id")
	private Integer state;

	@Column(name="org_id")
    private String orgId;
	
	public OrganizationState() {
		this.setId(java.util.UUID.randomUUID().toString());
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public OrganizationType getState() {
		return OrganizationType.getEnumValue(state);
	}

	public void setState(OrganizationType stateType) {
		this.state = (stateType != null)? stateType.getId() : null;
	}
	
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("orgId", this.orgId)
			.append("state", this.state).append("id", this.id).toString();
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
			
		OrganizationState other = (OrganizationState) obj;
		return Objects.equals(this.getId(), other.getId());
	}
}


