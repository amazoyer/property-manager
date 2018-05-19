package com.property.ui;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.property.manager.dal.PropertyDAL;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@Controller
public class PropertyController {

	static final String PROPERTY_VIEW = "properties";
	static final String PRICE_VIEW = "prices";

	@Inject
	public PropertyDAL propertyDAL;

	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String createPropertiesView(Model model) throws JsonParseException, JsonMappingException, IOException {

		return PROPERTY_VIEW;
	}

	@RequestMapping(value = "property/prices", method = RequestMethod.GET)
	public String createPriceView(Model model) throws JsonParseException, JsonMappingException, IOException {
		return PRICE_VIEW;
	}

	@RequestMapping(value = "/property/price/add", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> addPrice(@RequestBody Map<String, PropertyPrice> prices,
			HttpServletResponse response) throws IOException {
		for (Entry<String, PropertyPrice> price : prices.entrySet()){
			propertyDAL.addNewPropertyPrice(price.getKey(), price.getValue());
		}
		
		return Collections.singletonMap("message", "Price added");
	}

	@RequestMapping(value = "/property/update", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> updateProperties(@RequestBody Map<String, Property> properties,
			HttpServletResponse response) throws IOException {
		for(Entry<String, Property> property : properties.entrySet()){
			propertyDAL.updateProperty(property.getKey(), property.getValue());
		}
		return Collections.singletonMap("message", "Property Updated");
	}

	@RequestMapping(value = "/property/add", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> createProperty(@RequestBody Property property,
			HttpServletResponse response) throws IOException {
		propertyDAL.addNewProperty(property);
		return Collections.singletonMap("message", "Property added");
	}

	@RequestMapping(value = "/property/remove", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> deleteProperty(@RequestParam(value = "id") String id)
			throws IOException {
		propertyDAL.removeProperty(id);
		return Collections.singletonMap("message", "Property removed");
	}

	@RequestMapping(value = "/listProperties", method = RequestMethod.GET)
	public @ResponseBody Map<String, Property> listProperties() throws Exception {
		try {
			return propertyDAL.listAllProperties();
		} catch (IOException e) {
			throw new Exception("Cannot retrieve property");
		}
	}

	@RequestMapping(value = "/property", method = RequestMethod.GET)
	public @ResponseBody Property getProperty(@RequestParam(value = "id") String id) throws Exception {
		try {
			return propertyDAL.getPropertyByID(id);
		} catch (IOException e) {
			throw new Exception("Cannot retrieve property");
		}
	}

	@RequestMapping(value = "/property/listprices", method = RequestMethod.GET)
	public @ResponseBody List<PropertyPrice> getPricesForProperty(@RequestParam(value = "id") String propertyID)
			throws Exception {
		try {
			return propertyDAL.getPropertyByID(propertyID).getPrices();
		} catch (IOException e) {
			throw new Exception("Cannot retrieve property");
		}
	}
	


}
