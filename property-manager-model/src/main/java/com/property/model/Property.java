package com.property.model;

import java.util.ArrayList;
import java.util.List;

import javax.validation.constraints.Size;


/**
 * 
 * Represent a property with a list of price associated
 *
 */
public class Property {
	private String address;
	private String postcode;
	private String location;
	private Float surface;
	private Integer bedrooms;

	private List<PropertyPrice> propertyPrices = new ArrayList<PropertyPrice>();

	public String getAddress() {
		return address;
	}

	@Size(min=1)
	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostcode() {
		return postcode;
	}

	@Size(min=1)
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getLocation() {
		return location;
	}


	@Size(min=1)
	public void setLocation(String location) {
		this.location = location;
	}

	public Float getSurface() {
		return surface;
	}

	public void setSurface(Float surface) {
		this.surface = surface;
	}
	
	public Integer getBedrooms() {
		return bedrooms;
	}

	public void setBedrooms(Integer bedrooms) {
		this.bedrooms = bedrooms;
	}

	public List<PropertyPrice> getPrices() {
		return propertyPrices;
	}

}
