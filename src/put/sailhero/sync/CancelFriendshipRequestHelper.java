package put.sailhero.sync;

import org.apache.http.client.methods.HttpPost;

import put.sailhero.provider.SailHeroContract;
import android.content.Context;
import android.net.Uri;

public class CancelFriendshipRequestHelper extends FriendshipResponseRequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_CANCEL = "cancel";

	public CancelFriendshipRequestHelper(Context context, Integer friendshipId) {
		super(context, friendshipId);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_FRIENDSHIPS)
				.appendPath(mSentFriendshipId.toString())
				.appendPath(PATH_CANCEL)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	public void storeData() {
		mContext.getContentResolver().delete(
				SailHeroContract.Friendship.CONTENT_URI.buildUpon().appendPath(mSentFriendshipId.toString()).build(),
				null, null);
	}
}
