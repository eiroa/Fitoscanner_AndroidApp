package ar.edu.unq.fitoscanner.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Sample {

	private Long id;
	private String originDate;
	private List<Image> images;
	private String fieldName;
	private String sampleName;
	private String hash;
	private Boolean sent;
	private TreatmentResolution treatmentResolution;
	private LocationData locationData = new LocationData();
	private Integer requestTreatmentIntents;
	private Integer minutesFromLastRequest;
	private Boolean resolved;
	private Boolean valid;

	/**
	 * Constructor que deja Location data en null, 
	 * 
	 * @param id
	 * @param originDate
	 * @param images
	 * @param fieldName
	 * @param sampleName
	 */
	public Sample(Long id, String originDate, ArrayList<Image> images,
			String fieldName, String sampleName, String hash,Boolean sent,
			TreatmentResolution treatmentResolution,Integer reqs, Integer minutesPassed,
			Boolean resolved,Boolean valid) {
		this.id = id;
		this.originDate = originDate;
		this.images = images;
		this.fieldName = fieldName;
		this.sampleName = sampleName;
		this.hash = hash;
		this.sent = sent;
		this.treatmentResolution = treatmentResolution;
		this.requestTreatmentIntents = reqs;
		this.minutesFromLastRequest = minutesPassed;
		this.resolved = resolved;
		this.valid = valid;
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
			String city, String state, String country,String hash,Boolean sent,
			TreatmentResolution treatmentResolution,Integer reqs, Integer minutesPassed,
			Boolean resolved,Boolean valid) {
		this.id = id;
		this.originDate = originDate;
		this.images = images;
		this.fieldName = fieldName;
		this.sampleName = sampleName;
		this.locationData = new LocationData(lat, lon, city, state, country);
		this.hash = hash;
		this.sent = sent;
		this.treatmentResolution = treatmentResolution;
		this.requestTreatmentIntents = reqs;
		this.minutesFromLastRequest = minutesPassed;
		this.resolved = resolved;
		this.valid = valid;
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

	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
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
	
	public Boolean getSent() {
		return sent;
	}

	public void setSent(Boolean sent) {
		this.sent = sent;
	}
	

	public TreatmentResolution getTreatmentResolution() {
		return treatmentResolution;
	}

	public void setTreatmentResolution(TreatmentResolution treatmentResolution) {
		this.treatmentResolution = treatmentResolution;
	}

	public void setLocationData(String lat, String lon, String city,
			String state, String country) {
		this.locationData = new LocationData(lat, lon, city, state, country);
	}

	public Integer getRequestTreatmentIntents() {
		return requestTreatmentIntents;
	}

	public void setRequestTreatmentIntents(Integer requestTreatmentInstents) {
		this.requestTreatmentIntents = requestTreatmentInstents;
	}

	public Integer getMinutesFromLastRequest() {
		return minutesFromLastRequest;
	}

	public void setMinutesFromLastRequest(Integer minutesFromLastRequest) {
		this.minutesFromLastRequest = minutesFromLastRequest;
	}

	public Boolean getResolved() {
		return resolved;
	}

	public void setResolved(Boolean resolved) {
		this.resolved = resolved;
	}

	public Boolean getValid() {
		return valid;
	}

	public void setValid(Boolean valid) {
		this.valid = valid;
	}

	@Override
	public String toString() {
		return "Sample [id=" + id + ", originDate=" + originDate + ", images="
				+ images + ", fieldName=" + fieldName + ", sampleName="
				+ sampleName + ", hash=" + hash + ", sent=" + sent
				+ ", treatmentResolution=" + treatmentResolution.toString()
				+ ", locationData=" + locationData
				+ ", requestTreatmentIntents=" + requestTreatmentIntents
				+ ", minutesFromLastRequest=" + minutesFromLastRequest
				+ ", resolved=" + resolved + ", valid=" + valid + "]";
	}
	
	
	
}
