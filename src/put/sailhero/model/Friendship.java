package put.sailhero.model;

import org.json.simple.JSONObject;

import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.database.Cursor;

public class Friendship extends BaseModel {

	private Integer mId;
	private Integer mStatus;

	private User mFriend;

	public Friendship() {
	}

	public Friendship(JSONObject friendshipObject) {
		setId(Integer.valueOf(friendshipObject.get("id").toString()));
		setStatus(Integer.valueOf(friendshipObject.get("status").toString()));

		JSONObject friendObject = (JSONObject) friendshipObject.get("friend");
		mFriend = new User(friendObject);
	}

	public Friendship(Cursor c) {
		setId(c.getInt(Query.FRIENDSHIP_ID));
		setStatus(c.getInt(Query.FRIENDSHIP_STATUS));

		User friend = new User();
		friend.setId(c.getInt(Query.FRIENDSHIP_FRIEND_ID));
		friend.setEmail(c.getString(Query.FRIENDSHIP_FRIEND_EMAIL));
		friend.setName(c.getString(Query.FRIENDSHIP_FRIEND_NAME));
		friend.setSurname(c.getString(Query.FRIENDSHIP_FRIEND_SURNAME));
		friend.setAvatarUrl(c.getString(Query.FRIENDSHIP_FRIEND_AVATAR_URL));
		setFriend(friend);
	}

	public interface Query {
		String[] PROJECTION = {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_STATUS,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_AVATAR_URL
		};

		int FRIENDSHIP_ID = 0;
		int FRIENDSHIP_STATUS = 1;
		int FRIENDSHIP_FRIEND_ID = 2;
		int FRIENDSHIP_FRIEND_EMAIL = 3;
		int FRIENDSHIP_FRIEND_NAME = 4;
		int FRIENDSHIP_FRIEND_SURNAME = 5;
		int FRIENDSHIP_FRIEND_AVATAR_URL = 6;
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

	public User getFriend() {
		return mFriend;
	}

	public void setFriend(User friend) {
		mFriend = friend;
	}

	@Override
	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Friendship.COLUMN_NAME_ID, getId());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_STATUS, getStatus());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID, getFriend().getId());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL, getFriend().getEmail());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME, getFriend().getName());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME, getFriend().getSurname());
		values.put(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_AVATAR_URL, getFriend().getAvatarUrl());

		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Friendship)) {
			return false;
		}

		Friendship friendshipToCompare = (Friendship) o;

		return getId().equals(friendshipToCompare.getId()) && getStatus().equals(friendshipToCompare.getStatus())
				&& getFriend().equals(friendshipToCompare.getFriend());
	}
}
