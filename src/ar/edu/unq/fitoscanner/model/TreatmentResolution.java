package ar.edu.unq.fitoscanner.model;

import java.io.Serializable;
import java.util.List;

public class TreatmentResolution extends TreatmentResolutionRaw implements Serializable
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2046224991220210094L;
	private List<Image> specieImages;
	private List<Treatment> treatments;
	
	public List<Image> getSpecieImages() {
		return specieImages;
	}
	public void setSpecieImages(List<Image> specieImages) {
		this.specieImages = specieImages;
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
		setId(id);
		setSpecieName(specieName);
		setSpecieScientificName(specieScientificName);
		setSpecieDescription(specieDescription);
		this.specieImages = specieImages;
		setValid(valid);
		setResolved(resolved);
		setMessage(message);
		this.treatments = treatments;
	}
	public TreatmentResolution() {
	}
	public TreatmentResolution(Long treatmentResolutionId) {
		this.setId(treatmentResolutionId);
	}
	@Override
	public String toString() {
		return "TreatmentResolution [specieImages=" + specieImages
				+ ", treatments=" + treatments + ", getSpecieImages()="
				+ getSpecieImages() + ", getTreatments()=" + getTreatments()
				+ ", getId()=" + getId() + ", getSpecieName()="
				+ getSpecieName() + ", getSpecieScientificName()="
				+ getSpecieScientificName() + ", getSpecieDescription()="
				+ getSpecieDescription() + ", getValid()=" + getValid()
				+ ", getResolved()=" + getResolved() + ", getMessage()="
				+ getMessage() + ", getIdSpecieImages()=" + getIdSpecieImages()
				+ ", getIdTreatments()=" + getIdTreatments() + ", toString()="
				+ super.toString() + ", getClass()=" + getClass()
				+ ", hashCode()=" + hashCode() + "]";
	}
	
	
}
