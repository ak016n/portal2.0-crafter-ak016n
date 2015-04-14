package com.att.developer.controller;

import java.util.Map;

import javax.inject.Inject;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.att.developer.service.TemplateBasedEmailService;

@RestController
@RequestMapping("/cauth/message")
public class MessageController {

	@Inject
	private TemplateBasedEmailService templateBasedEmailService;
	
    @RequestMapping(value="/{templateName}", method = RequestMethod.POST)
    public void sendEmail(@PathVariable("templateName") String templateName, @RequestBody Map<String, Object> map) {
    	templateBasedEmailService.sendMail(templateName, map);
    }
	
}
