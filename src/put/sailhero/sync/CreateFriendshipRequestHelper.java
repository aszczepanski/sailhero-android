package put.sailhero.sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.Config;
import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.NullUserException;
import put.sailhero.exception.SameUserException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Friendship;
import android.content.Context;
import android.net.Uri;

public class CreateFriendshipRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String CREATE_FRIENDSHIP_REQUEST_PATH = "friendships";

	private Integer mSentFriendId;
	private Friendship mReceivedFriendship;

	public CreateFriendshipRequestHelper(Context context, Integer friendId) {
		super(context);

		mSentFriendId = friendId;
	}

	public Integer getSentFriendId() {
		return mSentFriendId;
	}

	public Friendship getReceivedFriendship() {
		return mReceivedFriendship;
	}

	@Override
	protected void createMethodClient() {
		final String apiHost = Config.API_HOST;
		final String apiPath = Config.API_PATH;
		final String version = Config.VERSION;
		final String i18n = Config.I18N;

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(CREATE_FRIENDSHIP_REQUEST_PATH)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();

		obj.put("friend_id", mSentFriendId);

		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((HttpPost) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, ForbiddenException,
			SameUserException, NullUserException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 201) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONObject friendshipObject = (JSONObject) obj.get("friendship");
				mReceivedFriendship = new Friendship(friendshipObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 403) {
			throw new ForbiddenException();
		} else if (statusCode == 462) {
			throw new SameUserException();
		} else if (statusCode == 463) {
			throw new NullUserException();
		} else {
			throw new SystemException("Invalid status code(" + statusCode + ")");
		}
	}

	@Override
	public void storeData() {
		// TODO: save using content resolver
	}
}
