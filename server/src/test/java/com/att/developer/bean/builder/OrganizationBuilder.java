package com.att.developer.bean.builder;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import com.att.developer.bean.Organization;
import com.att.developer.bean.OrganizationState;
import com.att.developer.bean.User;
import com.att.developer.typelist.OrgRelationshipType;
import com.att.developer.typelist.OrganizationType;

public class OrganizationBuilder {
		private Organization organization = new Organization();
		private Set<OrganizationState> organizationStates = new HashSet<>();
		
		public OrganizationBuilder() {
			organization.setId(java.util.UUID.randomUUID().toString());
			organization.setName("Big Bang Theory");
			organization.setDescription("serious show");
			organization.setRelationshipType(OrgRelationshipType.FIRST_PARTY);
			organizationStates.add(new OrganizationStateBuilder().withState(OrganizationType.PLAYGROUND).build());
			organization.setOrganizationStates(organizationStates);
			organization.setLastUpdated(Instant.now());
		}
		
		/**
		 * For non-default builder
		 */
		public OrganizationBuilder withVanillaOrganization() {
			organization = new Organization();
			return this;
		}
		
		public OrganizationBuilder withId(String id) {
			organization.setId(id);
			return this;
		}

		public OrganizationBuilder withName(String name) {
			organization.setName(name);
			return this;
		}

		public OrganizationBuilder withDescription(String description) {
			organization.setDescription(description);
			return this;
		}

		public OrganizationBuilder withLastUpdated(Instant lastUpdated) {
			organization.setLastUpdated(lastUpdated);
			return this;
		}
		
		public OrganizationBuilder withUsers(Set<User> users) {
			organization.setUsers(users);
			return this;
		}
		
		public OrganizationBuilder withUser(User user) {
			Set<User> users = new HashSet<>();
			users.add(user);
			organization.setUsers(users);
			return this;
		}

		public OrganizationBuilder withState(OrganizationType organizationStateType) {
			OrganizationState tempOrganizationState = new OrganizationState();
			tempOrganizationState.setState(organizationStateType);
			organizationStates.add(tempOrganizationState);
			return this;
		}
		
		public Organization build() {
			return organization;
		}
}
