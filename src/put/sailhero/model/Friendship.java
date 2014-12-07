package put.sailhero.model;

import org.json.simple.JSONObject;

public class Friendship {

	public static final int STATUS_NOT_ACCEPTED = 0;
	public static final int STATUS_ACCEPTED = 1;
	public static final int STATUS_BLOCKED = 2;

	private Integer mId;
	private Integer mStatus;

	private User mUser;
	private User mFriend;

	public Friendship() {
	}

	public Friendship(JSONObject friendshipObject) {
		setId(Integer.valueOf(friendshipObject.get("id").toString()));
		setStatus(Integer.valueOf(friendshipObject.get("status").toString()));

		JSONObject userObject = (JSONObject) friendshipObject.get("user");
		mUser = new User(userObject);

		JSONObject friendObject = (JSONObject) friendshipObject.get("friend");
		mFriend = new User(friendObject);

	}

	public Integer getId() {
		return mId;
	}

	public void setId(Integer id) {
		mId = id;
	}

	public Integer getStatus() {
		return mStatus;
	}

	public void setStatus(Integer status) {
		mStatus = status;
	}

	public User getUser() {
		return mUser;
	}

	public void setUser(User user) {
		mUser = user;
	}

	public User getFriend() {
		return mFriend;
	}

	public void setFriend(User friend) {
		mFriend = friend;
	}
}
