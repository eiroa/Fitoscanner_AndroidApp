package com.example.fitoscanner.model;

public class Image {
	
	private Long id;
	private String base64;
	private String title;
	private String Description;
	private Long idSample;
	
	public Image(Long id,String base64, String title, String desc, Long idSample){
		this.id = id;
		this.base64 =base64;
		this.title = title;
		this.Description = desc;
		this.idSample = idSample;
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
		return Description;
	}

	public void setDescription(String description) {
		Description = description;
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

	@Override
	public String toString(){
		return this.title + "\n" + this.Description;
	}

}
