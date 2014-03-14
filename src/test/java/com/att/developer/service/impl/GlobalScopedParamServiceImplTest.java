package com.att.developer.service.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.att.developer.bean.builder.AttPropertiesBuilder;
import com.att.developer.dao.AttPropertiesDAO;

public class GlobalScopedParamServiceImplTest {

	GlobalScopedParamServiceImpl globalScopedParamService;
	AttPropertiesDAO mockAttPropertiesDAO;
	
	@Before
	public void init() {
		mockAttPropertiesDAO = Mockito.mock(AttPropertiesDAO.class);
		
		globalScopedParamService = new GlobalScopedParamServiceImpl();
		globalScopedParamService.setAttPropertiesDAO(mockAttPropertiesDAO);
	}
	
	@Test
	public void testGetTrimmedStringArray() {
		String[] array = globalScopedParamService.getTrimmedStringArray("a,b,c,d");
		assertThat(array[0], is(equalTo("a")));
	}

	@Test
	public void testGet_string_single() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp(Mockito.anyString(), Mockito.anyString())).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("status");
		
		assertThat(value, is(equalTo("complex")));
	}
	
	@Test
	public void testGet_missingString() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp(Mockito.anyString(), Mockito.anyString())).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("unknown");
		
		assertThat(value, is(nullValue()));
	}
	
	@Test
	public void testGet_envSpecific() {
		System.setProperty("ENV", "INT");
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("ENV", "INT")).thenReturn(new AttPropertiesBuilder().withItemKey("ENV").withFieldKey("INT").withDescription("status=together").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("status");
		
		assertThat(value, is(equalTo("together")));
	}

	@Test
	public void testGetSet() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "SET")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("SET").withDescription("setOfValue=Set:[[a,b,c,d]]").build());
		
		globalScopedParamService.initialize();
		
		Set<String> value = globalScopedParamService.getSet("COLL","SET","setOfValue");
		
		Assert.assertTrue("contains value", value.contains("a"));
	}
	
	
	@Test
	public void testGetSet_withSpacesInKey() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "SET")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("SET").withDescription("setOfValue=Set : [[a,b,c,d]]").build()); // Spaces on Set{space}:{space}
		
		globalScopedParamService.initialize();
		
		Set<String> value = globalScopedParamService.getSet("COLL","SET","setOfValue");
		
		Assert.assertTrue("contains value", value.contains("a"));
	}
	
	@Test
	public void testGetSet_moreThanOne() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "SET")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("SET").withDescription("setOfValue=Set:[[a,b,c,d]]\nsecondIsString=string\nthirdIsASet=Set:[[thai, indian, mexi]]").build());
		
		globalScopedParamService.initialize();
		
		Set<String> value = globalScopedParamService.getSet("COLL","SET","thirdIsASet");
		
		Assert.assertTrue("contains value", value.contains("mexi"));
	}
	
	@Test
	public void testGetArray() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "ARRAY")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("ARRAY").withDescription("setOfValue=Array:[[a,b,c,d]]\nsecondIsString=string\nthirdIsASet=Set:[[thai, indian, mexi]]").build());
		
		globalScopedParamService.initialize();
		
		String[] value = globalScopedParamService.getArray("COLL","ARRAY","setOfValue");
		
		assertThat(value[0], is(equalTo("a")));
	}
	
	@Test
	public void testGetList() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "LIST")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("LIST").withDescription("setOfValue=LIST:[[a,b,c,d]]\nsecondIsString=string\nthirdIsASet=Set:[[thai, indian, mexi]]").build());
		
		globalScopedParamService.initialize();
		
		List<String> value = globalScopedParamService.getList("COLL","LIST","setOfValue");
		
		Assert.assertTrue("contains value", value.contains("a"));
	}
	
	@Test
	public void testGetMap() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "MAP")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("MAP").withDescription("setOfValue=Map:[[a=v1,b=v2,c=v3,d=v4]]\nsecondIsString=string\nthirdIsASet=Set:[[thai, indian, mexi]]").build());
		
		globalScopedParamService.initialize();
		
		Map<String, String> value = globalScopedParamService.getMap("COLL","MAP","setOfValue");
		
		assertThat(value.get("a"), is(equalTo("v1")));
	}
	
}
