package put.sailhero.sync;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.User;
import android.content.Context;
import android.net.Uri;

public class SearchUserRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String USERS_REQUEST_PATH = "users";

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
		final String apiHost = Config.API_HOST;
		final String apiPath = Config.API_PATH;
		final String version = Config.VERSION;
		final String i18n = Config.I18N;

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(USERS_REQUEST_PATH)
				.appendQueryParameter("q", mSentValue)
				.build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			try {
				String responseBody = EntityUtils.toString(mHttpResponse.getEntity());

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
			} catch (IOException e) {
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
