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
import com.property.exceptions.DataException;
import com.property.interfaces.IPropertyDAL;
import com.property.manager.dal.PropertyDAL;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@Controller
public class PropertyController {

	static final String PROPERTY_VIEW = "properties";
	static final String PRICE_VIEW = "prices";

	@Inject
	public IPropertyDAL propertyDAL;

	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String createPropertiesView(Model model) {
		return PROPERTY_VIEW;
	}

	@RequestMapping(value = "property/prices", method = RequestMethod.GET)
	public String createPriceView(Model model) {
		return PRICE_VIEW;
	}

	@RequestMapping(value = "/property/price/add", method = RequestMethod.POST)
	public @ResponseBody Map<String, String> addPrice(@RequestBody Map<String, PropertyPrice> prices,
			HttpServletResponse response) throws IOException {
		Entry<String, PropertyPrice> priceEntry = prices.entrySet().iterator().next();
		try {
			propertyDAL.addNewPropertyPrice(priceEntry.getKey(), priceEntry.getValue());
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot add price");
		}
		return Collections.singletonMap("message", "Prices added");
	}

	@RequestMapping(value = "/property/update", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> updateProperties(@RequestBody Map<String, Property> properties,
			HttpServletResponse response) throws IOException {
		Entry<String, Property> property = properties.entrySet().iterator().next();
		try {
			propertyDAL.updateProperty(property.getKey(), property.getValue());
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot update property");
		}
		return Collections.singletonMap("message", "Property Updated");
	}

	@RequestMapping(value = "/property/add", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> createProperty(@RequestBody Property property,
			HttpServletResponse response) throws IOException {
		try {
			propertyDAL.addNewProperty(property);
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot add property");
		}
		return Collections.singletonMap("message", "Property added");
	}

	@RequestMapping(value = "/property/remove", method = RequestMethod.POST)
	public @ResponseBody Map<String, ? extends Object> deleteProperty(@RequestParam(value = "id") String id)
			throws IOException {
		try {
			propertyDAL.removeProperty(id);
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot remove property");
		}
		return Collections.singletonMap("message", "Property removed");
	}

	@RequestMapping(value = "/listProperties", method = RequestMethod.GET)
	public @ResponseBody Map<String, ? extends Object> listProperties()  {
		try {
			return propertyDAL.listAllProperties();
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot list properties");
		}
	}

	@RequestMapping(value = "/property", method = RequestMethod.GET)
	public @ResponseBody  Object getProperty(@RequestParam(value = "id") String id) {
		try {
			return propertyDAL.getPropertyByID(id);
		} catch (DataException e) {
			return Collections.singletonMap("message", "Cannot get property");
		}
	}

}
