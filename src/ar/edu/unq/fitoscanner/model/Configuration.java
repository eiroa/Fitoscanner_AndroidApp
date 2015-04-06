package ar.edu.unq.fitoscanner.model;

/**
 * Representa la identidad configuracion
 * @author eiroa
 *
 */
public class Configuration {
	
	private Integer id;
	private String ip;

	public Configuration() {
	}
	public Configuration(Integer id, String ip) {
		this.id = id;
		this.ip = ip;
	}
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	

}
