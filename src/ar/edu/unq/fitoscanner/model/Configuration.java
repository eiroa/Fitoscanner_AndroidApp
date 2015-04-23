package ar.edu.unq.fitoscanner.model;

/**
 * Representa la identidad configuracion
 * @author eiroa
 *
 */
public class Configuration {
	
	private Integer id;
	private String ip;
	private String nick;
	private String pass;
	private String name;
	private String surname;

	public Configuration() {
	}
	
	
	public Configuration(Integer id, String ip, String nick, String pass,
			String name, String surname) {
		super();
		this.id = id;
		this.ip = ip;
		this.nick = nick;
		this.pass = pass;
		this.name = name;
		this.surname = surname;
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

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	

}
