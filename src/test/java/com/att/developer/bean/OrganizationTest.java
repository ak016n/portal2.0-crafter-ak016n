package com.att.developer.bean;

import java.util.HashSet;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.att.developer.bean.builder.OrganizationBuilder;
import com.att.developer.bean.builder.RoleBuilder;
import com.att.developer.bean.builder.UserBuilder;

public class OrganizationTest {

	@Test
	public void testGetOrganizationAdmin() {
		Role opaRole = new RoleBuilder().withId(Role.ROLE_ID_ORG_ADMIN).build();
					
		User userPenny = new UserBuilder().withLogin("penny_test").withRole(opaRole).build();
		User vanillaUser = new UserBuilder().withVanillaUser().build();
		Set<User> users = new HashSet<>();
		users.add(userPenny);
		users.add(vanillaUser);
		
		Organization org = new OrganizationBuilder().withUsers(users).build();
		User adminUser = null;
		for(int i = 0; i< 10; i++){
			adminUser = org.getOrganizationAdmin();
		}
		
		Assert.assertEquals("penny is NOT the admin", userPenny,  adminUser);
	}

}
