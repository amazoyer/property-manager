package com.property.ui;

import java.io.IOException;
import java.util.Collections;

import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.property.manager.dal.PropertyDAL;
import com.property.model.Property;


@Controller
public class PropertyController {
	
	static final String FORM_VIEW = "properties";
	
	@Inject
	public PropertyDAL propertyDAL;
	
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String createPropertiesView(Model model) throws JsonParseException, JsonMappingException, IOException {
		
		//Map<String, Property> properties = propertyDAL.searchByAddress("driac");
		//model.addAttribute("properties", properties);
		return FORM_VIEW;
	}
	
	@RequestMapping(value="/property", method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> createProperty(@RequestBody Property property, HttpServletResponse response) throws IOException {
		propertyDAL.addNewProperty(property);
		return Collections.singletonMap("message", "Property added");
	}
	
	@RequestMapping(value="/property/remove", method=RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> deleteProperty(
			@RequestParam(value = "id") String id) throws IOException {
		propertyDAL.removeProperty(id);
		return Collections.singletonMap("message", "Property removed");
	}
	
	@RequestMapping(value="/listProperties", method=RequestMethod.GET)
	public @ResponseBody Map<String, Property> searchPropertiesByAddress() throws Exception {
	    try {
			return propertyDAL.listAllProperties();
		} catch (IOException e) {
			throw new Exception("Cannot retrieve property");
		}
	}
	
}
