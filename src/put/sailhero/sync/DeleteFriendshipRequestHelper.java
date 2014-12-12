package put.sailhero.sync;

import org.apache.http.client.methods.HttpDelete;

import put.sailhero.provider.SailHeroContract;
import android.content.Context;
import android.net.Uri;

public class DeleteFriendshipRequestHelper extends FriendshipResponseRequestHelper {

	public DeleteFriendshipRequestHelper(Context context, Integer friendshipId) {
		super(context, friendshipId);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_FRIENDSHIPS)
				.appendPath(mSentFriendshipId.toString())
				.build();

		mHttpUriRequest = new HttpDelete(uri.toString());
	}

	@Override
	public void storeData() {
		mContext.getContentResolver().delete(
				SailHeroContract.Friendship.CONTENT_URI.buildUpon().appendPath(mSentFriendshipId.toString()).build(),
				null, null);
	}
}
