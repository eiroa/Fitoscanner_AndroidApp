package ar.edu.unq.fitoscanner.model;

import java.io.Serializable;
import java.util.List;

public class TreatmentResolutionRaw implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8554019034936825497L;
	private Long id;
	private String specieName;
	private String specieScientificName;
	private String specieDescription;
	private String  idSpecieImages;
	private Boolean valid;
	private Boolean resolved;
	private String message;
	private String idTreatments;
	
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
	public String getIdSpecieImages() {
		return idSpecieImages;
	}
	public void setIdSpecieImages(String idSpecieImages) {
		this.idSpecieImages = idSpecieImages;
	}
	public String getIdTreatments() {
		return idTreatments;
	}
	public void setIdTreatments(String idTreatments) {
		this.idTreatments = idTreatments;
	}
	public TreatmentResolutionRaw(Long id, String specieName,
			String specieScientificName, String specieDescription,
			String idSpecieImages, Boolean valid, Boolean resolved,
			String message, String idTreatments) {
		super();
		this.id = id;
		this.specieName = specieName;
		this.specieScientificName = specieScientificName;
		this.specieDescription = specieDescription;
		this.idSpecieImages = idSpecieImages;
		this.valid = valid;
		this.resolved = resolved;
		this.message = message;
		this.idTreatments = idTreatments;
	}
	
	
	
	public TreatmentResolutionRaw() {
		super();
	}
	
	@Override
	public String toString() {
		return "TreatmentResolutionRaw [id=" + id + ", specieName="
				+ specieName + ", specieScientificName=" + specieScientificName
				+ ", specieDescription=" + specieDescription
				+ ", idSpecieImages=" + idSpecieImages + ", valid=" + valid
				+ ", resolved=" + resolved + ", message=" + message
				+ ", idTreatments=" + idTreatments + "]";
	}
	
	
}
