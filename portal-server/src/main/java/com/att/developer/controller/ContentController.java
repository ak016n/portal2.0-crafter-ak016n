package com.att.developer.controller;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/content")
public class ContentController {
	
	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody String get() {
		return "{\"title\":\"title1\",\"contentGroups\":{\"contents\":[{\"title\":\"Symposium 2015\",\"text\":\"The 2015 Software Symposium theme 'Hello<Virtual/>World' introduces key direction-setting technologies at AT&T that align with 2015 Priorities for Business Integration, Network Leadership, Cost Infrastructure, and skills retooling. \",\"templateUrl\":\"jumbotron.tpl.html\"},{\"title1\":\"Big Data, Visualization, and Analysis\",\"text1\":\"This track includes a broad range of key technologies addressing challenges of managing and visualizing the information buried in AT&T's really big data stores. Included are these topics: \",\"title2\":\"Software Tool & Toolkits\",\"text2\":\"this includes tutorials and updates on the hottest technologies for building cool stuff. Included are these topics: \",\"title3\":\"Frameworks & Reuseable Components\",\"text3\":\"Big packages of open source applications and strategic capabilities often bring with them whole eco-systems. How-to presentations in this segment include: \",\"templateUrl\":\"subRow.tpl.html\"}]}}";
	}
	
	@RequestMapping(value="/{subPath}", method = RequestMethod.GET)
	public @ResponseBody String get(@PathVariable("subPath") String subPath) {
		String content = "{\"title\":\"title1\",\"contentGroups\":{\"contents\":[{\"title\":\"Symposium 2015\",\"text\":\"talks in detail about the importance of symposium\",\"templateUrl\":\"jumbotron.tpl.html\"},{\"title1\":\"Safari bug warning!!!!!\",\"text1\":\"As of v8.0, Safari exhibits a bug in which resizing your browser horizontally causes rendering errors in the justified nav that are cleared upon refreshing.\",\"title2\":\"Safari bug warning2!!!!!\",\"text2\":\"As of v8.0, Safari exhibits a bug in which resizing your browser horizontally causes rendering errors in the justified nav that are cleared upon refreshing.\",\"title3\": \"Safaribugwarning3!!!!!\",\"text3\": \"Asofv8.0,Safariexhibitsabuginwhichresizingyourbrowserhorizontallycausesrenderingerrorsinthejustifiednavthatarecleareduponrefreshing.\",\"templateUrl\": \"subRow.tpl.html\"}]}}";
		if(StringUtils.isNotBlank(subPath)) {
			switch (subPath) {
			case "home" :
				content = "{\"title\":\"title1\",\"contentGroups\":{\"contents\":[{\"title\":\"Symposium 2015\",\"text\":\"talks in detail about the importance of symposium\",\"templateUrl\":\"jumbotron.tpl.html\"},{\"title1\":\"Safari bug warning!!!!!\",\"text1\":\"As of v8.0, Safari exhibits a bug in which resizing your browser horizontally causes rendering errors in the justified nav that are cleared upon refreshing.\",\"title2\":\"Safari bug warning2!!!!!\",\"text2\":\"As of v8.0, Safari exhibits a bug in which resizing your browser horizontally causes rendering errors in the justified nav that are cleared upon refreshing.\",\"title3\": \"Safaribugwarning3!!!!!\",\"text3\":\"As of v8.0, Safari exhibits a bug in which resizing your browser horizontally causes rendering errors in the justified nav that are cleared upon refreshing.\",\"templateUrl\":\"subRow.tpl.html\"}]}}";
				break;
			case "details" :
				content = "details";
			}
			
		}
		return content;
	}
}
	