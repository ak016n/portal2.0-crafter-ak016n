package com.att.developer.service.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasItem;

import java.util.List;
import java.util.Map;

import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.EventLog;
import com.att.developer.bean.builder.AttPropertiesBuilder;
import com.att.developer.dao.AttPropertiesDAO;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.EventTrackingService;

public class GlobalScopedParamServiceImplTest {

	@InjectMocks
	GlobalScopedParamServiceImpl globalScopedParamService;
	
	@Mock
	private AttPropertiesDAO mockAttPropertiesDAO;
	
	@Mock
	private EventTrackingService mockEventTrackingService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetTrimmedStringArray() {
		String[] array = globalScopedParamService.getTrimmedStringArray("a,b,c,d");
		Assert.assertEquals(array[0], "a");
	}

	@Test
	public void testGet_string_single() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp(Mockito.anyString(), Mockito.anyString())).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("status");
		
		Assert.assertEquals(value, "complex");
	}
	
	@Test
	public void testGet_missingString() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp(Mockito.anyString(), Mockito.anyString())).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("unknown");
		
		Assert.assertNull(value);
	}
	
	@Test
	public void testGet_envSpecific() {
		System.setProperty("ENV", "INT");
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("ENV", "INT")).thenReturn(new AttPropertiesBuilder().withItemKey("ENV").withFieldKey("INT").withDescription("\"status\":\"together\"").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("status");
		
		Assert.assertEquals(value, "together");
	}

	@Test
	public void testGetSet_moreThanOne() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "SET")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("SET").withDescription("{\r\n    \"setOfValue\": [\r\n        \"a\",\r\n        \"b\",\r\n        \"c\",\r\n        \"d\"\r\n    ],\r\n    \"secondIsString\": \"string\",\r\n    \"thirdIsASet\": [\r\n        \"thai\",\r\n        \"indian\",\r\n        \"mexi\"\r\n    ]\r\n}").build());
		
		globalScopedParamService.initialize();
		
		List<String> value = globalScopedParamService.getList("COLL","SET","thirdIsASet");
		
		Assert.assertTrue("contains value", value.contains("mexi"));
	}
	
	@Test
	public void testGetList() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "LIST")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("LIST").withDescription("{\"setOfValue\": [\"a\",\"b\",\"c\"],\"secondIsString\": \"string\",\"thirdIsASet\": [\"thai\",\"indian\",\"mexi\"]}").build());
		
		globalScopedParamService.initialize();
		
		List<String> value = globalScopedParamService.getList("COLL","LIST","setOfValue");
		
		Assert.assertTrue("contains value", value.contains("a"));
	}
	
	@Test
	public void testGetMap() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp("GLOBAL", "DEFAULT")).thenReturn(new AttPropertiesBuilder().withItemKey("GLOBAL").withFieldKey("DEFAULT").build());
		Mockito.when(mockAttPropertiesDAO.findActiveProp("COLL", "MAP")).thenReturn(new AttPropertiesBuilder().withItemKey("COLL").withFieldKey("MAP").withDescription("{\"setOfValue\": {\"a\": \"v1\",\"b\": \"v2\",\"c\": \"v3\",\"d\": \"v4\"},\"secondIsString\": \"string\",\"thirdIsASet\": [\"thai\",\"indian\",\"mexi\"]}").build());
		
		globalScopedParamService.initialize();
		
		Map<String, String> value = globalScopedParamService.getMap("COLL","MAP","setOfValue");
		
		Assert.assertEquals(value.get("a"), "v1");
	}
	
	@Test
	public void testCreate_happyPath() {
		
		AttProperties attProperties = new AttPropertiesBuilder().build();

		Mockito.when(mockAttPropertiesDAO.create(attProperties)).thenReturn(attProperties);

		globalScopedParamService.createProperties(attProperties, "actor_id");
		
		Mockito.verify(mockEventTrackingService, Mockito.atLeastOnce()).globalPropertiesChangeEvent(Mockito.any(EventLog.class));
		Mockito.verify(mockAttPropertiesDAO, Mockito.atLeastOnce()).create(attProperties);
	}
	
	@Test(expected=DuplicateDataException.class)
	public void testCreate_duplicateDataException() {
		
		AttProperties attProperties = new AttPropertiesBuilder().build();

		PersistenceException exception = new PersistenceException(new DAOException(new com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException()));
		
		Mockito.when(mockAttPropertiesDAO.create(attProperties)).thenThrow(exception);

		globalScopedParamService.createProperties(attProperties, "actor_id");
		
		Mockito.verify(mockEventTrackingService, Mockito.never()).globalPropertiesChangeEvent(Mockito.any(EventLog.class));
		Mockito.verify(mockAttPropertiesDAO, Mockito.atLeastOnce()).create(attProperties);
	}
	
	@Test(expected=DAOException.class)
	public void testCreate_daoException() {
		
		AttProperties attProperties = new AttPropertiesBuilder().build();

		PersistenceException exception = new PersistenceException();
		
		Mockito.when(mockAttPropertiesDAO.create(attProperties)).thenThrow(exception);

		globalScopedParamService.createProperties(attProperties, "actor_id");
		
		Mockito.verify(mockEventTrackingService, Mockito.never()).globalPropertiesChangeEvent(Mockito.any(EventLog.class));
		Mockito.verify(mockAttPropertiesDAO, Mockito.atLeastOnce()).create(attProperties);
	}
	
	@Test
	public void testUpdate_happyPath() {
		AttProperties attProperties = new AttPropertiesBuilder().build();
		AttProperties updateAttProperties = new AttPropertiesBuilder().withDescription("status=normal").build();
		
		Mockito.when(mockAttPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey())).thenReturn(attProperties);
		Mockito.when(mockAttPropertiesDAO.create(Mockito.any(AttProperties.class))).thenReturn(updateAttProperties);
		
		globalScopedParamService.updateProperties(updateAttProperties);
		
		Mockito.verify(mockAttPropertiesDAO, Mockito.atLeastOnce()).create(Mockito.any(AttProperties.class));
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUpdate_withOutAnyChanges() {
		AttProperties attProperties = new AttPropertiesBuilder().build();
		Mockito.when(mockAttPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey())).thenReturn(attProperties);
		
		try {
			globalScopedParamService.updateProperties(attProperties);
		} catch(UnsupportedOperationException e) {
			Assert.assertEquals(GlobalScopedParamServiceImpl.NO_CHANGE_MSG, e.getMessage());
			throw e;
		}
		Assert.fail();
	}
	
	@Test(expected=DAOException.class)
	public void testUpdate_withNoOriginalValue() {
		Mockito.when(mockAttPropertiesDAO.findActiveProp(Mockito.anyString(), Mockito.anyString())).thenReturn(null);
		
		try {
			globalScopedParamService.updateProperties(new AttPropertiesBuilder().build());
		} catch(DAOException e) {
			Assert.assertEquals(GlobalScopedParamServiceImpl.TRY_AGAIN_LATER_MSG, e.getMessage());
			throw e;
		}
		Assert.fail();
	}
	
	@Test(expected=UnsupportedOperationException.class)
	public void testUpdate_onDeleted() {
		AttProperties attProperties = new AttPropertiesBuilder().withDelete(true).build();
		Mockito.when(mockAttPropertiesDAO.findActiveProp(attProperties.getItemKey(), attProperties.getFieldKey())).thenReturn(attProperties);
		
		try {
			globalScopedParamService.updateProperties(attProperties);
		} catch(UnsupportedOperationException e) {
			Assert.assertEquals(GlobalScopedParamServiceImpl.ALREADY_DELETED_MSG, e.getMessage());
			throw e;
		}
		Assert.fail();
	}
	
	@Test
	public void testDelete() {
		AttProperties attProperties = new AttPropertiesBuilder().build();
		globalScopedParamService.deleteProperties(attProperties);
		attProperties.setDeleted(true);
		Mockito.verify(mockAttPropertiesDAO, Mockito.atMost(1)).update(attProperties);
	}
	
	@Test
	public void testGetPropertiesMapFromText_string() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("{\"x\" : \"y\"}");
		
		Assert.assertEquals("y", map.get("x"));
	}
	
	
	@Test
	public void testGetPropertiesMapFromText_list() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("{ \"x\": [\"y\",\"z\"]}");
		
		Assert.assertTrue(map.get("x") instanceof List);
		@SuppressWarnings("unchecked")
		List<String> listOfStrings = (List<String>) map.get("x");
		assertThat(listOfStrings, hasItem("y") );
	}
	
	
	@Test
	public void testGetPropertiesMapFromText_map() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("{\"x\": {\"y\": \"z\"}}");
		
		Assert.assertTrue(map.get("x") instanceof Map);
		@SuppressWarnings("unchecked")
		Map<String, String> mapOfStrings = (Map<String, String>) map.get("x");
		assertThat(mapOfStrings, hasEntry("y", "z") );
	}
	
	@Test
	public void testGetPropertiesMapFromText_missingStartBrackets() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("\r\n \"x\": [\"y\",\"z\"]}");
		
		Assert.assertTrue(map.get("x") instanceof List);
		@SuppressWarnings("unchecked")
		List<String> listOfStrings = (List<String>) map.get("x");
		assertThat(listOfStrings, hasItem("y") );
	}
	
	@Test
	public void testGetPropertiesMapFromText_missingEndBrackets() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("{ \"x\": [\"y\",\"z\"]");
		
		Assert.assertTrue(map.get("x") instanceof List);
		@SuppressWarnings("unchecked")
		List<String> listOfStrings = (List<String>) map.get("x");
		assertThat(listOfStrings, hasItem("y") );
	}
	
	@Test
	public void testGetPropertiesMapFromText_missingBothBrackets() {
		Map<String, Object> map = globalScopedParamService.getPropertiesMapFromText("\"x\": [\"y\",\"z\"]");
		
		Assert.assertTrue(map.get("x") instanceof List);
		@SuppressWarnings("unchecked")
		List<String> listOfStrings = (List<String>) map.get("x");
		assertThat(listOfStrings, hasItem("y") );
	}
}
