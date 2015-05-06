package ar.edu.unq.fitoscanner.model;

import java.util.List;

public class TreatmentResolution extends TreatmentResolutionRaw{
	
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
}
