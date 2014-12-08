package put.sailhero.sync;

import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.net.Uri;

public class BlockFriendshipRequestHelper extends FriendshipResponseRequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_BLOCK = "block";

	public BlockFriendshipRequestHelper(Context context, Integer friendshipId) {
		super(context, friendshipId);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_FRIENDSHIPS)
				.appendPath(mSentFriendshipId.toString())
				.appendPath(PATH_BLOCK)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	public void storeData() {
		// TODO: for testing purposes wait for gcm

		//		ContentValues values = new ContentValues();
		//		values.put(SailHeroContract.Friendship.COLUMN_NAME_STATUS, SailHeroContract.Friendship.STATUS_BLOCKED);
		//
		//		mContext.getContentResolver().update(
		//				SailHeroContract.Friendship.CONTENT_URI.buildUpon().appendPath(mSentFriendshipId.toString()).build(),
		//				values, null, null);
	}
}
