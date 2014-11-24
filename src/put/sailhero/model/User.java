package put.sailhero.model;

import org.json.simple.JSONObject;

public class User {
	private Integer id;
	private String email;
	private String name;
	private String surname;

	public User() {
	}

	public User(JSONObject userObject) {
		this();

		setId(Integer.valueOf(userObject.get("id").toString()));
		setEmail(userObject.get("email").toString());
		setName(userObject.get("name").toString());
		setSurname(userObject.get("surname").toString());
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
