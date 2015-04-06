package ar.edu.unq.fitoscanner.model;

import java.util.ArrayList;
import java.util.Date;

public class Sample {

	private Long id;
	private String originDate;
	private ArrayList<Image> images;
	private String fieldName;
	private String sampleName;
	private String hash;
	private LocationData locationData = new LocationData();

	/**
	 * Constructor que deja Location data en null
	 * 
	 * @param id
	 * @param originDate
	 * @param images
	 * @param fieldName
	 * @param sampleName
	 */
	public Sample(Long id, String originDate, ArrayList<Image> images,
			String fieldName, String sampleName, String hash) {
		this.id = id;
		this.originDate = originDate;
		this.images = images;
		this.fieldName = fieldName;
		this.sampleName = sampleName;
		this.hash = hash;
	}

	/**
	 * Constructor que genera internamente el LocationData
	 * 
	 * @param id
	 * @param originDate
	 * @param images
	 * @param fieldName
	 * @param sampleName
	 * @param lat
	 * @param lon
	 * @param city
	 * @param state
	 * @param country
	 */
	public Sample(Long id, String originDate, ArrayList<Image> images,
			String fieldName, String sampleName, String lat, String lon,
			String city, String state, String country,String hash) {
		this.id = id;
		this.originDate = originDate;
		this.images = images;
		this.fieldName = fieldName;
		this.sampleName = sampleName;
		this.locationData = new LocationData(lat, lon, city, state, country);
		this.hash = hash;
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

	public String getSampleName() {
		return sampleName;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public void setSampleName(String sampleName) {
		this.sampleName = sampleName;
	}

	public LocationData getLocationData() {
		return locationData;
	}

	public void setLocationData(LocationData locationData) {
		this.locationData = locationData;
	}

	public void setLocationData(String lat, String lon, String city,
			String state, String country) {
		this.locationData = new LocationData(lat, lon, city, state, country);
	}

}
