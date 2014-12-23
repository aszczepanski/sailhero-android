package put.sailhero.sync;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.User;
import android.content.Context;
import android.net.Uri;

public class SearchUserRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_USERS = "users";

	private String mSentValue;

	private LinkedList<User> mRetrievedUsers;

	public SearchUserRequestHelper(Context context, String value) {
		super(context);
		mSentValue = value;
	}

	public LinkedList<User> getRetrievedUsers() {
		return mRetrievedUsers;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_USERS).appendQueryParameter("q", mSentValue).build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONArray usersArray = (JSONArray) obj.get("users");

				LinkedList<User> users = new LinkedList<User>();
				for (int i = 0; i < usersArray.size(); i++) {
					JSONObject userObject = (JSONObject) usersArray.get(i);
					User user = new User(userObject);

					users.addLast(user);
				}

				mRetrievedUsers = users;

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			} catch (org.apache.http.ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}

	@Override
	public void storeData() throws SystemException {
	}
}
