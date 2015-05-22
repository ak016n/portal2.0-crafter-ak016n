package com.att.developer.bean;

import org.junit.Assert;
import org.junit.Test;

public class AttPropertiesTest {

	@Test
	public void testAttPropertiesConstructor() {
		AttProperties attProperties = new AttProperties("item_key", "field_key", "desc", 1);
		Assert.assertEquals("Should be All CAPITAL - itemKey", attProperties.getItemKey(), "ITEM_KEY");
		Assert.assertEquals("Should be All CAPITAL - fieldKey", attProperties.getFieldKey(), "FIELD_KEY");
	}

	@Test
	public void testGetItemKey() {
		AttProperties attProperties = new AttProperties();
		attProperties.setItemKey("item_keY");
		Assert.assertEquals("Should be All CAPITAL - itemKey", attProperties.getItemKey(), "ITEM_KEY");
	}

	@Test
	public void testGetFieldKey() {
		AttProperties attProperties = new AttProperties();
		attProperties.setFieldKey("field_KEy");
		Assert.assertEquals("Should be All CAPITAL - fieldKey", attProperties.getFieldKey(), "FIELD_KEY");
	}

	@Test
	public void testGetItemKey_null() {
		AttProperties attProperties = new AttProperties();
		attProperties.setItemKey(null);
		Assert.assertEquals("Should be null", attProperties.getItemKey(), null);
	}

	@Test
	public void testGetFieldKey_null() {
		AttProperties attProperties = new AttProperties();
		attProperties.setFieldKey(null);
		Assert.assertEquals("Should be null", attProperties.getFieldKey(), null);
	}
}
