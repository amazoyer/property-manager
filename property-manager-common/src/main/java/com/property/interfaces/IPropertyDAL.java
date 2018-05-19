package com.property.interfaces;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.property.exceptions.DataException;
import com.property.model.Property;
import com.property.model.PropertyPrice;

public interface IPropertyDAL {
	
	
	public void addNewProperty(Property property) throws DataException;

	public void addNewPropertyPrice(String propertyId, PropertyPrice price) throws DataException;

	public Map<String, Property> listAllProperties() throws DataException;

	public Map<String, Property> searchByAddress(String address) throws DataException;

	public Property getPropertyByID(String id) throws DataException;

	public void removeProperty(String id) throws DataException;

	public void updateProperty(String id, Property partialProperty) throws DataException;
}
