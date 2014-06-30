package com.att.developer.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.util.FaultUtils;

@Controller
@RequestMapping("/admin")
@SessionAttributes({"attProperties"})
public class AdminController {
	
	@Inject
	private GlobalScopedParamService globalScopedParamService;
	
	public static final String ADMIN_URL = "/adminConsole/admin.html";
    
	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model) {
		AttProperties attProperties = new AttProperties();
		model.addAttribute("attProperties", attProperties);
		return ADMIN_URL;
	}
	
	@RequestMapping(value="/{itemKey}/{fieldKey}", method = RequestMethod.GET)
	public @ResponseBody AttProperties getProperty(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey, @RequestParam(value="version", required=false) String version) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		AttProperties attProperties = null;
		if(StringUtils.isBlank(version)) {
			attProperties = globalScopedParamService.getProperties(itemKey, fieldKey);
		} else {
			attProperties = globalScopedParamService.getProperties(itemKey, fieldKey, version);
		}
		
		if (attProperties == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for item key/field key combination").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return attProperties;
	}
	
	@RequestMapping(value="/search/{itemKey}", method = RequestMethod.GET)
	public @ResponseBody List<String> getIK(@PathVariable("itemKey") String itemKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Search term for itemKey cannot be null").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> searchResults = globalScopedParamService.search(itemKey);
		
		if (searchResults == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for search term").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return searchResults;
	}
	
	@RequestMapping(value="/search/{itemKey}/{fieldKey}", method = RequestMethod.GET)
	public @ResponseBody List<String> getFK(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> searchResults = globalScopedParamService.search(itemKey, fieldKey);
		
		if (searchResults == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for search term").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return searchResults;
	}
	
	@RequestMapping(value="/{itemKey}/{fieldKey}/versions", method = RequestMethod.GET)
	public @ResponseBody Map<String, List<String>> versionInformation(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> versionColl = globalScopedParamService.getVersions(itemKey, fieldKey);

		if (versionColl == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for item key/field key combination").build();
			throw new ServerSideException(errorColl.add(error));
		}

		Map<String, List<String>> versionMap = new HashMap<>();
		versionMap.put("versions", versionColl);
		return versionMap;
	}

    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody AttProperties createProperty(@RequestBody @Valid AttProperties attProperties, BindingResult bindingResult) {
		AttProperties lclAttProperties = null;
		ServerSideErrors errorColl = new ServerSideErrors();
		boolean violationsPresent = FaultUtils.checkForViolations(false, bindingResult, errorColl);
		
		if(violationsPresent) {
			throw new ServerSideException(errorColl);
		} else {
			try {
				lclAttProperties = globalScopedParamService.createProperties(attProperties);
			} catch (DuplicateDataException e) {
				ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Record already exist. Please use update").build();
				throw new ServerSideException(errorColl.add(error));
			} catch (DAOException e) {
				ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Error creating record. Reason: " + e.getMessage()).build();
				throw new ServerSideException(errorColl.add(error));
			} 
		}
		
		return lclAttProperties;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public @ResponseBody AttProperties updateProperty(@RequestBody @Valid AttProperties attProperties, BindingResult bindingResult) {
    	AttProperties lclAttProperties = null;
		ServerSideErrors errorColl = new ServerSideErrors();
		boolean violationsPresent = FaultUtils.checkForViolations(false, bindingResult, errorColl);
		
		if(violationsPresent) {
			throw new ServerSideException(errorColl);
		} else {
			try {
				lclAttProperties = globalScopedParamService.updateProperties(attProperties);
			} catch (DAOException e) {
				ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Error updating record. Reason: " + e.getMessage()).build();
				throw new ServerSideException(errorColl.add(error));
			}
		}
		
		return lclAttProperties;
    }

    
    @RequestMapping(value="/{itemKey}/{fieldKey}", method = RequestMethod.DELETE)
    public @ResponseBody AttProperties deleteProperty(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		ServerSideErrors errorColl = new ServerSideErrors();
		
		AttProperties lclAttProperties = getProperty(itemKey, fieldKey, null);
		try {
			lclAttProperties = globalScopedParamService.deleteProperties(lclAttProperties);
		} catch (DAOException e) {
			ServerSideError error = new ServerSideError.Builder()
					.id("ssGeneralError")
					.message("Error deleting record. Reason: " + e.getMessage())
					.build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		return lclAttProperties;
    }
}
