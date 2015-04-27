package ar.edu.unq.fitoscanner.model;

public class TreatmentResolution{
	
	
	private Long id;
	private String specieName;
	private String specieScientificName;
	private String specieDescription;
	private String message;
	private Boolean valid;
	private Boolean resolved;
	private String idsSpecieImages;
	private Treatment treatment;
	
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


	public String getMessage() {
		return message;
	}


	public void setMessage(String message) {
		this.message = message;
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


	public String getIdsSpecieImages() {
		return idsSpecieImages;
	}

	public void setIdsSpecieImages(String idsSpecieImages) {
		this.idsSpecieImages = idsSpecieImages;
	}

	public Treatment getTreatment() {
		return treatment;
	}

	public void setTreatment(Treatment treatment) {
		this.treatment = treatment;
	}
	
}
