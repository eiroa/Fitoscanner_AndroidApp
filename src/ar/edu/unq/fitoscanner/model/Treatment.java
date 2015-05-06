package ar.edu.unq.fitoscanner.model;

import java.util.List;

public class Treatment{
	
	
	private Long id;
	private String name;
	private String description;
	private String idImages; // EJ:"1-5-3"
	private List<Image> images;
	private String unit;
	private String unitType;
	private String frequency;
	private String frequencyType;
	private String extraLink1;
	private String extraLink2;
	private String extraLink3;
	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getIdImages() {
		return idImages;
	}


	public void setIdImages(String idImages) {
		this.idImages = idImages;
	}


	public String getUnit() {
		return unit;
	}


	public void setUnit(String unit) {
		this.unit = unit;
	}


	public String getUnitType() {
		return unitType;
	}


	public void setUnitType(String unitType) {
		this.unitType = unitType;
	}


	public String getFrequency() {
		return frequency;
	}


	public void setFrequency(String frequency) {
		this.frequency = frequency;
	}


	public String getFrequencyType() {
		return frequencyType;
	}


	public void setFrequencyType(String frequencyType) {
		this.frequencyType = frequencyType;
	}


	public String getExtraLink1() {
		return extraLink1;
	}


	public void setExtraLink1(String extraLink1) {
		this.extraLink1 = extraLink1;
	}


	public String getExtraLink2() {
		return extraLink2;
	}


	public void setExtraLink2(String extraLink2) {
		this.extraLink2 = extraLink2;
	}


	public String getExtraLink3() {
		return extraLink3;
	}


	public void setExtraLink3(String extraLink3) {
		this.extraLink3 = extraLink3;
	}


	public List<Image> getImages() {
		return images;
	}


	public void setImages(List<Image> images) {
		this.images = images;
	}


	public Treatment(Long id, String name, String description,
			List<Image> images, String unit, String unitType, String frequency,
			String frequencyType, String extraLink1, String extraLink2,
			String extraLink3) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.images = images;
		this.unit = unit;
		this.unitType = unitType;
		this.frequency = frequency;
		this.frequencyType = frequencyType;
		this.extraLink1 = extraLink1;
		this.extraLink2 = extraLink2;
		this.extraLink3 = extraLink3;
	}


	@Override
	public String toString() {
		return "Treatment [id=" + id + ", name=" + name + ", description="
				+ description + ", images=" + images + ", unit=" + unit
				+ ", unitType=" + unitType + ", frequency=" + frequency
				+ ", frequencyType=" + frequencyType + ", extraLink1="
				+ extraLink1 + ", extraLink2=" + extraLink2 + ", extraLink3="
				+ extraLink3 + "]";
	}

	
	
	
	

}
