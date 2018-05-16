package com.property.model;

import java.time.LocalDate;

public class PropertyPrice {
	private float price;
	private LocalDate date;

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

}
