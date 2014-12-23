package put.sailhero.sync;

import org.apache.http.client.methods.HttpPost;

import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public class AcceptFriendshipRequestHelper extends FriendshipResponseRequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_ACCEPT = "accept";

	public AcceptFriendshipRequestHelper(Context context, Integer friendshipId) {
		super(context, friendshipId);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_FRIENDSHIPS)
				.appendPath(mSentFriendshipId.toString())
				.appendPath(PATH_ACCEPT)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	public void storeData() {
		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Friendship.COLUMN_NAME_STATUS, SailHeroContract.Friendship.STATUS_ACCEPTED);

		mContext.getContentResolver().update(
				SailHeroContract.Friendship.CONTENT_URI.buildUpon().appendPath(mSentFriendshipId.toString()).build(),
				values, null, null);
	}
}
