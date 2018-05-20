package com.property.ui;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.http.ResponseEntity;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.property.exceptions.DataException;
import com.property.interfaces.IPropertyDAL;
import com.property.model.Property;
import com.property.model.PropertyPrice;

@Controller
public class PropertyController {

	static final String PROPERTY_VIEW = "properties";
	static final String PRICE_VIEW = "prices";

	@Inject
	public IPropertyDAL propertyDAL;

	
	/*
	 * Two different view : properties and price 
	 */
	@RequestMapping(value = "/properties", method = RequestMethod.GET)
	public String createPropertiesView(Model model) {
		return PROPERTY_VIEW;
	}
	@RequestMapping(value = "property/prices", method = RequestMethod.GET)
	public String createPriceView(Model model) {
		return PRICE_VIEW;
	}


	/*
	 * 
	 * Write operations on properties
	 * 
	 */
	@RequestMapping(value = "/property/add", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> createProperty(@Valid @RequestBody Property property , Errors errors) throws IOException {
		if (errors.hasErrors()){
            return ResponseEntity.badRequest().body("Cannot add property");
		}
		try {
			propertyDAL.addNewProperty(property);
		} catch (DataException e) {
            return ResponseEntity.badRequest().body("Cannot add property");
		}
        return ResponseEntity.ok("Property added");
	}

	@RequestMapping(value = "/property/update", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> updateProperties(@Valid @RequestBody Map<String, Property> properties,
			Errors errors) throws Exception {
		Entry<String, Property> property = properties.entrySet().iterator().next();
		if (errors.hasErrors()){
            return ResponseEntity.badRequest().body("Cannot update property");
		}
		try {
			propertyDAL.updateProperty(property.getKey(), property.getValue());
		} catch (DataException e) {
            return ResponseEntity.badRequest().body("Cannot update property");
		}
        return ResponseEntity.ok("Property updated");
	}


	@RequestMapping(value = "/property/remove", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> deleteProperty(@RequestParam(value = "id") String id)
			throws IOException {
	
		try {
			propertyDAL.removeProperty(id);
		} catch (DataException e) {
			return ResponseEntity.badRequest().body("Cannot remove property");
		}
        return ResponseEntity.ok("Property removed");
	}



	@RequestMapping(value = "/property/price/add", method = RequestMethod.POST)
	public @ResponseBody ResponseEntity<?> addPrice(@Valid @RequestBody Map<String, PropertyPrice> prices, Errors errors) throws IOException {
		Entry<String, PropertyPrice> priceEntry = prices.entrySet().iterator().next();
		if (errors.hasErrors()){
            return ResponseEntity.badRequest().body("Cannot add price");
		}
		try {
			propertyDAL.addNewPropertyPrice(priceEntry.getKey(), priceEntry.getValue());
		} catch (DataException e) {
            return ResponseEntity.badRequest().body("Cannot add price");
		}
        return ResponseEntity.ok("Price added");
        
	}

	/*
	 * 
	 * Read operations on properties
	 * 
	 */
	@RequestMapping(value = "/listProperties", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> listProperties(HttpServletResponse response) {
		try {
			return ResponseEntity.ok(propertyDAL.listAllProperties());
		} catch (DataException e) {
			return ResponseEntity.badRequest().body("Cannot list properties");
		}
	}

	@RequestMapping(value = "/property", method = RequestMethod.GET)
	public @ResponseBody ResponseEntity<?> getProperty(@RequestParam(value = "id") String id, HttpServletResponse response) {
		try {
			return ResponseEntity.ok(propertyDAL.getPropertyByID(id));
		} catch (DataException e) {
			return ResponseEntity.badRequest().body("Cannot get property");
		}
	}
	

}
