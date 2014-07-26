package com.att.developer.controller;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.validation.BindingResult;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.builder.AttPropertiesBuilder;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.exception.ServerSideException;
import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.GlobalScopedParamService;

public class AdminControllerTest {

	private static final String NO_WAY_MSG = "no way";
	private static final String FIELD_KEY_LOWER = "y";
	private static final String ITEM_KEY_LOWER = "x";
	private static final String FIELD_KEY_CAPS = "Y";
	private static final String ITEM_KEY_CAPS = "X";

	AdminController adminController = null;
	
	@Mock
	private GlobalScopedParamService mockGlobalScopedParamService;
	
	@Mock
	private BindingResult mockBindingResult;
	
	@Mock
	private Principal mockPrincipal;
	
	@Before
	public void init() {
		MockitoAnnotations.initMocks(this);
		adminController = new AdminController();
		adminController.setGlobalScopedParamService(mockGlobalScopedParamService);
	}
	
	@Test
	public void testGetProperty_withVersion() {
		Mockito.when(mockGlobalScopedParamService.getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS, "1")).thenReturn(new AttPropertiesBuilder().build());
		
		AttProperties attProperties = adminController.getProperty(ITEM_KEY_CAPS, FIELD_KEY_CAPS, "1");
		
		Assert.assertNotNull(attProperties);
		Mockito.verify(mockGlobalScopedParamService, Mockito.never()).getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
	}
	
	@Test
	public void testGetProperty_withOutVersion() {
		Mockito.when(mockGlobalScopedParamService.getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(new AttPropertiesBuilder().build());
		
		AttProperties attProperties = adminController.getProperty(ITEM_KEY_CAPS, FIELD_KEY_CAPS, null);
		
		Assert.assertNotNull(attProperties);
		Mockito.verify(mockGlobalScopedParamService, Mockito.never()).getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS, "1");
	}
	
	@Test(expected=ServerSideException.class)
	public void testGetProperty_withMissingKeyData() {
		adminController.getProperty(null, FIELD_KEY_CAPS, null);
	}
	
	
	@Test(expected=ServerSideException.class)
	public void testGetProperty_noMatch() {
		Mockito.when(mockGlobalScopedParamService.getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(null);
		
		adminController.getProperty(ITEM_KEY_LOWER, FIELD_KEY_CAPS, null);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
	}
	
	@Test(expected=ServerSideException.class)
	public void testGetIK_missingIK() {
		adminController.getIK(null);
	}
	
	@Test(expected=ServerSideException.class)
	public void testGetIK_noMatch() {
		Mockito.when(mockGlobalScopedParamService.search(ITEM_KEY_CAPS)).thenReturn(null);
		
		adminController.getIK(ITEM_KEY_LOWER);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).search(ITEM_KEY_CAPS);
	}
	
	@Test
	public void testGetIK_happyPath() {
		Mockito.when(mockGlobalScopedParamService.search(ITEM_KEY_CAPS)).thenReturn(Arrays.asList(ITEM_KEY_CAPS, "XX"));
		
		List<String> searchIKResultColl = adminController.getIK(ITEM_KEY_CAPS);
		
		Assert.assertNotNull(searchIKResultColl);
		Assert.assertTrue(searchIKResultColl.size() == 2);
	}
	
	
	@Test(expected=ServerSideException.class)
	public void testGetFK_missingFK() {
		adminController.getFK(ITEM_KEY_CAPS , null);
	}
	
	@Test(expected=ServerSideException.class)
	public void testGetFK_noMatch() {
		Mockito.when(mockGlobalScopedParamService.search(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(null);
		
		adminController.getFK(ITEM_KEY_LOWER, FIELD_KEY_CAPS);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).search(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
	}
	
	@Test
	public void testGetFK_happyPath() {
		Mockito.when(mockGlobalScopedParamService.search(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(Arrays.asList(FIELD_KEY_CAPS, "YY"));
		
		List<String> searchFKResultColl = adminController.getFK(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
		
		Assert.assertNotNull(searchFKResultColl);
		Assert.assertTrue(searchFKResultColl.size() == 2);
	}
	
	@Test(expected=ServerSideException.class)
	public void testVersion_missing() {
		adminController.versionInformation(null , null);
	}
	
	@Test(expected=ServerSideException.class)
	public void testVersion_noMatch() {
		Mockito.when(mockGlobalScopedParamService.getVersions(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(null);
		
		adminController.versionInformation(ITEM_KEY_LOWER, FIELD_KEY_CAPS);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).getVersions(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
	}
	
	@Test
	public void testVersion_happyPath() {
		Mockito.when(mockGlobalScopedParamService.getVersions(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(Arrays.asList("1", "2"));
		
		Map<String, List<String>> versionMap = adminController.versionInformation(ITEM_KEY_LOWER, FIELD_KEY_LOWER);
		
		Assert.assertNotNull(versionMap.values());
		Assert.assertTrue(versionMap.get("versions").contains("1"));
	}
	
	@Test
	public void testCreate_happyPath() {
		Mockito.when(mockPrincipal.getName()).thenReturn("sheldon");
		
		Mockito.when(mockGlobalScopedParamService.createProperties(Mockito.any(AttProperties.class), Mockito.anyString())).thenReturn(new AttProperties());
		
		AttProperties attProperties = adminController.createProperty(new AttPropertiesBuilder().build(), mockBindingResult, mockPrincipal);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).createProperties(Mockito.any(AttProperties.class), Mockito.anyString());
		Assert.assertNotNull(attProperties);
	}
	
	@Test(expected=ServerSideException.class)
	public void testCreate_withViolations() {
		Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);
		adminController.createProperty(new AttPropertiesBuilder().build(), mockBindingResult, mockPrincipal);
	}
	
	@Test(expected = ServerSideException.class)
	public void testCreate_duplicateData() {
		Mockito.when(mockPrincipal.getName()).thenReturn("sheldon");
		
		Mockito.when(mockGlobalScopedParamService.createProperties(Mockito.any(AttProperties.class), Mockito.anyString())).thenThrow(new DuplicateDataException(NO_WAY_MSG));
		
		adminController.createProperty(new AttPropertiesBuilder().build(), mockBindingResult, mockPrincipal);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).createProperties(Mockito.any(AttProperties.class), Mockito.anyString());
	}
	
	@Test(expected = ServerSideException.class)
	public void testCreate_daoException() {
		Mockito.when(mockGlobalScopedParamService.createProperties(Mockito.any(AttProperties.class), Mockito.anyString())).thenThrow(new DAOException(NO_WAY_MSG));
		
		adminController.createProperty(new AttPropertiesBuilder().build(), mockBindingResult, mockPrincipal);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).createProperties(Mockito.any(AttProperties.class), Mockito.anyString());
	}
	
	@Test
	public void testUpdate_happyPath() {
		Mockito.when(mockPrincipal.getName()).thenReturn("sheldon");
		
		Mockito.when(mockGlobalScopedParamService.updateProperties(Mockito.any(AttProperties.class))).thenReturn(new AttProperties());
		
		AttProperties attProperties = adminController.updateProperty(new AttPropertiesBuilder().build(), mockBindingResult);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).updateProperties(Mockito.any(AttProperties.class));
		Assert.assertNotNull(attProperties);
	}
	
	@Test(expected=ServerSideException.class)
	public void testUpdate_withViolations() {
		Mockito.when(mockBindingResult.hasErrors()).thenReturn(true);
		adminController.updateProperty(new AttPropertiesBuilder().build(), mockBindingResult);
	}
	
	@Test(expected = ServerSideException.class)
	public void testUpdate_UnsupportedOpException() {
		Mockito.when(mockPrincipal.getName()).thenReturn("sheldon");
		
		Mockito.when(mockGlobalScopedParamService.updateProperties(Mockito.any(AttProperties.class))).thenThrow(new UnsupportedOperationException(NO_WAY_MSG));
		
		adminController.updateProperty(new AttPropertiesBuilder().build(), mockBindingResult);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).updateProperties(Mockito.any(AttProperties.class));
	}
	
	@Test(expected = ServerSideException.class)
	public void testUpdate_daoException() {
		Mockito.when(mockGlobalScopedParamService.updateProperties(Mockito.any(AttProperties.class))).thenThrow(new DAOException(NO_WAY_MSG));
		
		adminController.updateProperty(new AttPropertiesBuilder().build(), mockBindingResult);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).updateProperties(Mockito.any(AttProperties.class));
	}
	
	@Test
	public void testDelete_happyPath() {
		Mockito.when(mockGlobalScopedParamService.deleteProperties(Mockito.any(AttProperties.class))).thenReturn(new AttProperties());
		Mockito.when(mockGlobalScopedParamService.getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(new AttPropertiesBuilder().build());
		
		adminController.deleteProperty(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).deleteProperties(Mockito.any(AttProperties.class));
	}
	
	
	@Test(expected = ServerSideException.class)
	public void testDelete_daoException() {
		Mockito.when(mockGlobalScopedParamService.deleteProperties(Mockito.any(AttProperties.class))).thenThrow(new DAOException(NO_WAY_MSG));
		Mockito.when(mockGlobalScopedParamService.getProperties(ITEM_KEY_CAPS, FIELD_KEY_CAPS)).thenReturn(new AttPropertiesBuilder().build());
		
		adminController.deleteProperty(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).deleteProperties(Mockito.any(AttProperties.class));
	}
	
	@Test(expected = ServerSideException.class)
	public void testDelete_nonExistent() {
		Mockito.when(mockGlobalScopedParamService.deleteProperties(Mockito.any(AttProperties.class))).thenReturn(new AttProperties());
		
		adminController.deleteProperty(ITEM_KEY_CAPS, FIELD_KEY_CAPS);
		
		Mockito.verify(mockGlobalScopedParamService, Mockito.atLeastOnce()).deleteProperties(Mockito.any(AttProperties.class));
	}
}
