package com.att.developer.service.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.PersistenceException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
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

	GlobalScopedParamServiceImpl globalScopedParamService;
	
	@Mock
	private AttPropertiesDAO mockAttPropertiesDAO;
	
	@Mock
	private EventTrackingService mockEventTrackingService;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		globalScopedParamService = new GlobalScopedParamServiceImpl();
		globalScopedParamService.setAttPropertiesDAO(mockAttPropertiesDAO);
		globalScopedParamService.setEventTrackingService(mockEventTrackingService);
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
		Mockito.when(mockAttPropertiesDAO.findActiveProp("ENV", "INT")).thenReturn(new AttPropertiesBuilder().withItemKey("ENV").withFieldKey("INT").withDescription("status=together").build());
		
		globalScopedParamService.initialize();
		
		String value = globalScopedParamService.get("status");
		
		Assert.assertEquals(value, "together");
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
		
		Assert.assertEquals(value[0], "a");
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
}
