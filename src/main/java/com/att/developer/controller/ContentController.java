package com.att.developer.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/content")
public class ContentController {
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String get() {
		return "{\"title\": \"title1\",\"contentGroups\": {\"contents\": [{\"title\": \"header1\",\"text\": \"hello\",\"templateUrl\": \"contentTemplate1.html\"},{\"title\": \"header2\",\"text\": \"world\",\"templateUrl\": \"contentTemplate2.html\"}]}}";
	}

}
