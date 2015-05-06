package ar.edu.unq.fitoscanner.model;

public class Image {
	
	private Long id;
	private Long idSample;
	private Long idTreatment;
	private Long idTreatmentResolution;
	private String title;
	private String description;
	private String base64;
	
	
	public Image(Long id, Long idSample, Long idTreatment,Long idTreatmentResolution,String title, String desc,String base64){
		this.id = id;
		this.idSample = idSample;	
		this.idTreatment = idTreatment;
		this.idTreatmentResolution = idTreatmentResolution;
		this.title = title;
		this.description = desc;
		this.base64 =base64;
		
	}

	public String getTitle() {
		return title;
	}
		
	public void setTitle(String title) {
		this.title = title;
	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
	public Long getIdSample() {
		return idSample;
	}

	public void setIdSample(Long idSample) {
		this.idSample = idSample;
	}

	public Long getIdTreatment() {
		return idTreatment;
	}

	public void setIdTreatment(Long idTreatment) {
		this.idTreatment = idTreatment;
	}
	
	public Long getIdTreatmentResolution() {
		return idTreatmentResolution;
	}

	public void setIdTreatmentResolution(Long idTreatmentResolution) {
		this.idTreatmentResolution = idTreatmentResolution;
	}

	@Override
	public String toString(){
		return this.title + "\n" + this.description;
	}

}
