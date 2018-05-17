package com.property.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.property.manager.dal.PropertyDAL;
import com.property.model.Property;



@Controller
public class PropertiesController {
	private static final String FORM_VIEW = "properties";

	@Inject
	private PropertyDAL propertyDAL;

	/**
	 * Create the form
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String createForm(Model model) throws JsonParseException, JsonMappingException, IOException {
		addDisplayPropertiesToModel(model);
		return FORM_VIEW;
	}

	private void addDisplayPropertiesToModel(Model model) throws JsonParseException, JsonMappingException, IOException {
		List<Property> properties = propertyDAL.searchPropertyByAddress("driac");
		model.addAttribute("properties", properties);
	}


	


}
