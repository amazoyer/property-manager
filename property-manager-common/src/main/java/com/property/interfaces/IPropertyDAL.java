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

	/**
	 * 
	 * Update an existing property
	 * 
	 * @param id : existing property to update
	 * @param partialProperty : contains only fields that need to be updated
	 * @throws DataException
	 */
	public void updateProperty(String id, Property partialProperty) throws DataException;

	
	/**
	 * 
	 * Link a new price for an existing property
	 * 
	 * @param propertyId
	 * @param price
	 * @throws DataException
	 */
	public void addNewPropertyPrice(String propertyId, PropertyPrice price) throws DataException;

	/**
	 * 
	 * Perform a fulltext search on address field 
	 * 
	 * @param address
	 * @return a map of properties : { "id" : property }
	 * @throws DataException
	 */
	public Map<String, Property> searchByAddress(String address) throws DataException;

	public Map<String, Property> listAllProperties() throws DataException;
	
	public Property getPropertyByID(String id) throws DataException;

	public void removeProperty(String id) throws DataException;
}
