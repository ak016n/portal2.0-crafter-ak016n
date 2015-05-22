package com.att.developer.bean.builder;

import com.att.developer.bean.OrganizationState;
import com.att.developer.typelist.OrganizationType;

public class OrganizationStateBuilder {

	private OrganizationState organizationState = new OrganizationState();

	public OrganizationStateBuilder() {
		organizationState.setState(OrganizationType.PLAYGROUND);
	}

	public OrganizationStateBuilder withState(OrganizationType organizationStateType) {
		organizationState.setState(organizationStateType);
		return this;
	}
	
	public OrganizationState build() {
		return organizationState;
	}
	
}
