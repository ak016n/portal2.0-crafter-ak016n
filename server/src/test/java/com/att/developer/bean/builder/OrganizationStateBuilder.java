package com.att.developer.bean.builder;

import com.att.developer.bean.OrganizationState;
import com.att.developer.typelist.OrganizationStateType;

public class OrganizationStateBuilder {

	private OrganizationState organizationState = new OrganizationState();

	public OrganizationStateBuilder() {
		organizationState.setState(OrganizationStateType.PLAYGROUND);
	}

	public OrganizationStateBuilder withState(OrganizationStateType organizationStateType) {
		organizationState.setState(organizationStateType);
		return this;
	}
	
	public OrganizationState build() {
		return organizationState;
	}
	
}
