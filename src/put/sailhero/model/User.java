package put.sailhero.model;

import org.json.simple.JSONObject;

public class User {
	private Integer mId;
	private String mEmail;
	private String mName;
	private String mSurname;
	private String mAvatarUrl;

	public User() {
	}

	public User(JSONObject userObject) {
		this();

		setId(((Number) (userObject.get("id"))).intValue());
		setEmail((String) userObject.get("email"));
		setName((String) userObject.get("name"));
		setSurname((String) userObject.get("surname"));
		setAvatarUrl((String) userObject.get("avatar_url"));
	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}

	public String getEmail() {
		return mEmail;
	}

	public void setEmail(String email) {
		mEmail = email;
	}

	public String getSurname() {
		return mSurname;
	}

	public void setSurname(String surname) {
		mSurname = surname;
	}

	public String getName() {
		return mName;
	}

	public void setName(String name) {
		mName = name;
	}

	public String getAvatarUrl() {
		return mAvatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		mAvatarUrl = avatarUrl;
	}
}
