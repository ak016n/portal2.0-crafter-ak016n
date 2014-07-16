package com.att.developer.bean;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang3.builder.ToStringBuilder;

import com.att.developer.typelist.OrganizationStateType;

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

	public OrganizationStateType getState() {
		return OrganizationStateType.getEnumValue(state);
	}

	public void setState(OrganizationStateType stateType) {
		this.state = (stateType != null)? stateType.getId() : null;
	}
	
	public String getOrgId() {
		return orgId;
	}

	public void setOrgId(String orgId) {
		this.orgId = orgId;
	}

	public String toString() {
		return new ToStringBuilder(this).append("orgId", this.orgId)
			.append("state", this.state).append("id", this.id).toString();
	}
}


