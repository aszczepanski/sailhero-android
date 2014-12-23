package put.sailhero.sync;

import java.io.IOException;

import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import android.content.Context;

public abstract class FriendshipResponseRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	protected final static String PATH_FRIENDSHIPS = "friendships";

	protected Integer mSentFriendshipId;

	public FriendshipResponseRequestHelper(Context context, Integer friendshipId) {
		super(context);

		mSentFriendshipId = friendshipId;
	}

	public Integer getSentFriendshipId() {
		return mSentFriendshipId;
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, ForbiddenException, NotFoundException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		try {
			mHttpResponse.getEntity().consumeContent();
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			// friendship accepted
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 403) {
			throw new ForbiddenException();
		} else if (statusCode == 404) {
			throw new NotFoundException();
		} else {
			throw new SystemException("Invalid status code(" + statusCode + ")");
		}
	}
}
