package put.sailhero.model;

import org.json.simple.JSONObject;

import android.text.TextUtils;

public class User {
	private Integer mId;
	private String mEmail;
	private String mName;
	private String mSurname;
	private String mAvatarUrl;
	private Boolean mSharePosition;

	public User() {
	}

	public User(JSONObject userObject) {
		this();

		setId(((Number) (userObject.get("id"))).intValue());
		setEmail((String) userObject.get("email"));
		setName((String) userObject.get("name"));
		setSurname((String) userObject.get("surname"));
		setAvatarUrl((String) userObject.get("avatar_url"));
		setSharePosition((Boolean) userObject.get("share_position"));
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

	public Boolean getSharePosition() {
		return mSharePosition;
	}

	public void setSharePosition(Boolean sharePosition) {
		mSharePosition = sharePosition;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof User)) {
			return false;
		}

		User userToCompare = (User) o;

		return getId().equals(userToCompare.getId()) && TextUtils.equals(getName(), userToCompare.getName())
				&& TextUtils.equals(getSurname(), userToCompare.getSurname())
				&& TextUtils.equals(getEmail(), userToCompare.getEmail())
				&& TextUtils.equals(getAvatarUrl(), userToCompare.getAvatarUrl());
	}
}
