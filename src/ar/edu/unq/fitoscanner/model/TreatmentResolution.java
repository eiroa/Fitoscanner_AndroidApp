package ar.edu.unq.fitoscanner.model;

import java.util.List;

public class TreatmentResolution{
	
	
	private Long id;
	private String specieName;
	private String specieScientificName;
	private String specieDescription;
	private List<Image> specieImages;
	private Boolean valid;
	private Boolean resolved;
	private String message;
	private List<Treatment> treatments;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getSpecieName() {
		return specieName;
	}
	public void setSpecieName(String specieName) {
		this.specieName = specieName;
	}
	public String getSpecieScientificName() {
		return specieScientificName;
	}
	public void setSpecieScientificName(String specieScientificName) {
		this.specieScientificName = specieScientificName;
	}
	public String getSpecieDescription() {
		return specieDescription;
	}
	public void setSpecieDescription(String specieDescription) {
		this.specieDescription = specieDescription;
	}
	public List<Image> getSpecieImages() {
		return specieImages;
	}
	public void setSpecieImages(List<Image> specieImages) {
		this.specieImages = specieImages;
	}
	public Boolean getValid() {
		return valid;
	}
	public void setValid(Boolean valid) {
		this.valid = valid;
	}
	public Boolean getResolved() {
		return resolved;
	}
	public void setResolved(Boolean resolved) {
		this.resolved = resolved;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public List<Treatment> getTreatments() {
		return treatments;
	}
	public void setTreatments(List<Treatment> treatments) {
		this.treatments = treatments;
	}
	
	public TreatmentResolution(Long id, String specieName,
			String specieScientificName, String specieDescription,
			List<Image> specieImages, Boolean valid, Boolean resolved,
			String message, List<Treatment> treatments) {
		super();
		this.id = id;
		this.specieName = specieName;
		this.specieScientificName = specieScientificName;
		this.specieDescription = specieDescription;
		this.specieImages = specieImages;
		this.valid = valid;
		this.resolved = resolved;
		this.message = message;
		this.treatments = treatments;
	}
	@Override
	public String toString() {
		return "TreatmentResolution [id=" + id + ", specieName=" + specieName
				+ ", specieScientificName=" + specieScientificName
				+ ", specieDescription=" + specieDescription
				+ ", specieImages=" + specieImages + ", valid=" + valid
				+ ", resolved=" + resolved + ", message=" + message
				+ ", treatments=" + treatments + "]";
	}
	
	
	
	
	
	
	
	
}
