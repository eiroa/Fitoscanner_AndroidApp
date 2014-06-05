package com.example.fitoscanner.model;

import java.util.ArrayList;
import java.util.Date;
public class Sample {
	
	private Long id;
	private String originDate;
	private ArrayList<Image>images;
	private String fieldName;
	
	public Sample(Long id, String originDate, ArrayList<Image> images,
			String fieldName) {
		this.id = id;
		this.originDate = originDate;
		this.images = images;
		this.fieldName = fieldName;
	}



	public Sample() {
	}



	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getOriginDate() {
		return originDate;
	}
	public void setOriginDate(String originDate) {
		this.originDate = originDate;
	}
	public ArrayList<Image> getImages() {
		return images;
	}
	public void setImages(ArrayList<Image> images) {
		this.images = images;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	
	
	

}
