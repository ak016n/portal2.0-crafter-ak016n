package com.att.developer.controller;

import javax.inject.Inject;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;

import com.att.developer.bean.AttProperties;
import com.att.developer.bean.ServerSideError;
import com.att.developer.bean.ServerSideErrors;
import com.att.developer.exception.ServerSideException;
import com.att.developer.service.GlobalScopedParamService;
import com.att.developer.util.FaultUtils;

@Controller
@RequestMapping("/admin")
@SessionAttributes({"attProperties"})
public class AdminController {
	
	@Inject
	private GlobalScopedParamService globalScopedParamService;
	
	public static final String ADMIN_URL = "/adminConsole/admin.jsp";
    
	@RequestMapping(method = RequestMethod.GET)
	public String get(Model model) {
		AttProperties attProperties = new AttProperties();
		model.addAttribute("attProperties", attProperties);
		return ADMIN_URL;
	}
	
	@RequestMapping(value="/{itemKey}/{fieldKey}", method = RequestMethod.GET)
	public @ResponseBody AttProperties refresh(@PathVariable("itemKey") String itemKey, @PathVariable("fieldKey") String fieldKey) {
		
		ServerSideErrors errorColl = new ServerSideErrors();
		
		if(StringUtils.isBlank(itemKey) || StringUtils.isBlank(fieldKey)) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("Both Item key and Field key are required").build();
			throw new ServerSideException(errorColl.add(error));
		}
		
		AttProperties attProperties = globalScopedParamService.getProperties(itemKey, fieldKey);

		if (attProperties == null) {
			ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for item key/field key combination").build();
			throw new ServerSideException(errorColl.add(error));
		}

		return attProperties;
	}

    @RequestMapping(method = RequestMethod.POST)
    public AttProperties create(@Valid AttProperties attProperties, BindingResult bindingResult, Model model) {
		AttProperties lclAttProperties = null;
		ServerSideErrors errorColl = new ServerSideErrors();
		boolean violationsPresent = FaultUtils.checkForViolations(false, bindingResult, errorColl);
		
		if(violationsPresent) {
			throw new ServerSideException(errorColl);
		} else {
			lclAttProperties = globalScopedParamService.createProperties(attProperties);
			
			if(lclAttProperties == null) {
				ServerSideError error = new ServerSideError.Builder().id("ssGeneralError").message("No data found for item key/field key combination").build();
				throw new ServerSideException(errorColl.add(error));
			}
		}
		
		return lclAttProperties;
    }

    @RequestMapping(method = RequestMethod.PUT)
    public String update(@Valid AttProperties attProperties, Model model) {
       	attProperties = globalScopedParamService.getProperties(attProperties.getItemKey(), attProperties.getFieldKey());
    	
    	model.addAttribute("attProperties", attProperties);
        return ADMIN_URL;
    }

}
