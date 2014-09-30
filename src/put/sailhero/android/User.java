package put.sailhero.android;

public class User {
	private Integer id;
	private String createdAt;  // TODO: change to datetime type
	private String email;
	private Integer yachId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Integer getYachId() {
		return yachId;
	}

	public void setYachId(Integer yachId) {
		this.yachId = yachId;
	}
}
