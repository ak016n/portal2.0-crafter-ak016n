package com.att.developer.bean.builder;

import com.att.developer.bean.AttProperties;

public class AttPropertiesBuilder {

	private AttProperties attProperties = new AttProperties();

	public AttPropertiesBuilder() {
		attProperties.setItemKey("leonard");
		attProperties.setFieldKey("penny");
		attProperties.setDescription("status=complex");
		attProperties.setVersion(1);
	}

	/**
	 * For non-default builder
	 */
	public AttPropertiesBuilder withNonDefault() {
		attProperties = new AttProperties();
		return this;
	}

	public AttPropertiesBuilder withId(String id) {
		attProperties.setId(id);
		return this;
	}
	
	public AttPropertiesBuilder withItemKey(String itemKey) {
		attProperties.setItemKey(itemKey);
		return this;
	}

	public AttPropertiesBuilder withFieldKey(String fieldKey) {
		attProperties.setFieldKey(fieldKey);
		return this;
	}

	public AttPropertiesBuilder withDescription(String description) {
		attProperties.setDescription(description);
		return this;
	}

	public AttPropertiesBuilder withVersion(int version) {
		attProperties.setVersion(version);
		return this;
	}
	
	public AttPropertiesBuilder withDelete(boolean deleted) {
		attProperties.setDeleted(deleted);
		return this;
	}
	
	
	public AttProperties build() {
		return attProperties;
	}

}
