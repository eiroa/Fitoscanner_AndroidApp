package com.example.fitoscanner.model;

import java.util.ArrayList;
import java.util.Date;
public class Sample {
	
	private Long id;
	private Date originDate;
	private ArrayList<Image>images;
	private String fieldName;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getOriginDate() {
		return originDate;
	}
	public void setOriginDate(Date originDate) {
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
