package com.att.developer.bean.builder;

import org.springframework.context.ApplicationContext;

import com.att.developer.bean.api.Api;
import com.att.developer.dao.ApiDAO;

public class ApiBuilder {

	private Api api = new Api();

	public ApiBuilder() {
		api.setId(java.util.UUID.randomUUID().toString());
		api.setName("SMS");
	}

	public Api build() {
		return api;
	}

	public ApiBuilder withId(String id) {
		this.api.setId(id);
		return this;
	}

	public ApiBuilder withName(String name) {
		this.api.setName(name);
		return this;
	}

	public Api create(ApplicationContext context) {
		ApiDAO apiDAO = (ApiDAO) context.getBean("jpaApiDAOImpl");
        Api api = apiDAO.create(this.build());
        //apiDAO.getEntityManager().refresh(api);
        return api;
	}
}