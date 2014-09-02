package com.att.developer.controller;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.bean.SessionUser;
import com.att.developer.bean.User;
import com.att.developer.exception.DAOException;
import com.att.developer.exception.DuplicateDataException;
import com.att.developer.exception.ServerSideException;
import com.att.developer.exception.UnsupportedOperationException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.util.FaultUtils;

@RestController
@RequestMapping("/admin")
public class AdminController {
	
	@Inject
	private GlobalScopedParamService globalScopedParamService;
	
	public void setGlobalScopedParamService(GlobalScopedParamService globalScopedParamService) {
		this.globalScopedParamService = globalScopedParamService;
	}

	@RequestMapping(value="/{itemKey}/{fieldKey}", method = RequestMethod.GET)
	public AttProperties getProperty(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey, @RequestParam(value="version", required=false) String version) {
		
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
	public List<String> getIK(@PathVariable("itemKey") String itemKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Search term for itemKey cannot be null").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> searchResults = globalScopedParamService.search(StringUtils.upperCase(itemKey));
		
		if (searchResults == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for search term").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return searchResults;
	}
	
	@RequestMapping(value="/search/{itemKey}/{fieldKey}", method = RequestMethod.GET)
	public List<String> getFK(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> searchResults = globalScopedParamService.search( StringUtils.upperCase(itemKey),  StringUtils.upperCase(fieldKey));
		
		if (searchResults == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for search term").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return searchResults;
	}
	
	@RequestMapping(value="/{itemKey}/{fieldKey}/versions", method = RequestMethod.GET)
	public Map<String, List<String>> versionInformation(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		List<String> versionColl = globalScopedParamService.getVersions(StringUtils.upperCase(itemKey),  StringUtils.upperCase(fieldKey));

		if (versionColl == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for item key/field key combination").build();
			throw new ServerSideException(errorColl.add(error));
		}

		Map<String, List<String>> versionMap = new HashMap<>();
		versionMap.put("versions", versionColl);
		return versionMap;
	}

    @RequestMapping(method = RequestMethod.POST)
    public AttProperties createProperty(@RequestBody @Valid AttProperties attProperties, BindingResult bindingResult, Principal principal) {
		AttProperties lclAttProperties = null;
		ServerSideErrors errorColl = new ServerSideErrors();
		boolean violationsPresent = FaultUtils.checkForViolations(false, bindingResult, errorColl);
		
		if(violationsPresent) {
			throw new ServerSideException(errorColl);
		} else {
			try {
				User user = getUserFromSecurityContext();
				lclAttProperties = globalScopedParamService.createProperties(attProperties, (user != null) ? user.getId() : principal.getName());
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
    public AttProperties updateProperty(@RequestBody @Valid AttProperties attProperties, BindingResult bindingResult) {
    	AttProperties lclAttProperties = null;
		ServerSideErrors errorColl = new ServerSideErrors();
		boolean violationsPresent = FaultUtils.checkForViolations(false, bindingResult, errorColl);
		
		if(violationsPresent) {
			throw new ServerSideException(errorColl);
		} else {
			try {
				lclAttProperties = globalScopedParamService.updateProperties(attProperties);
			} catch (DAOException | UnsupportedOperationException e) {
				ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Error updating record. Reason: " + e.getMessage()).build();
				throw new ServerSideException(errorColl.add(error));
			}
		}
		
		return lclAttProperties;
    }

    
    @RequestMapping(value="/{itemKey}/{fieldKey}", method = RequestMethod.DELETE)
    public AttProperties deleteProperty(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		ServerSideErrors errorColl = new ServerSideErrors();
		
		AttProperties lclAttProperties = getProperty(itemKey, fieldKey, null);
		
		if(lclAttProperties == null) {
			ServerSideError error = new ServerSideError.Builder()
			.id("ssGeneralError")
			.message("Non-existing account")
			.build();
			throw new ServerSideException(errorColl.add(error));
		}
		
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
    
    private User getUserFromSecurityContext() {
    	User user = null;
    	Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    	if(auth!= null && !(auth instanceof AnonymousAuthenticationToken)) {
    		SessionUser userDetails = (SessionUser) auth.getPrincipal();
    		user = userDetails.getUser();
    	}
    	return user;
    }
    
}
