package com.property.model;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

public class PropertyPrice {
	private Float price;
	
	private Date date;

	public Float getPrice() {
		return price;
	}

	public void setPrice(Float price) {
		this.price = price;
	}

	 @JsonFormat
     (shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public Date getDate() {
		return date;
	}
	 @JsonFormat
     (shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy")
	public void setDate(Date date) {
		this.date = date;
	}

}
