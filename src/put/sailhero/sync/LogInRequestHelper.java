package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.Config;
import put.sailhero.exception.InvalidResourceOwnerException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.User;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class LogInRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private static final String PATH_OAUTH = "oauth";
	private static final String PATH_TOKEN = "token";

	private String mSentUsername;
	private String mSentPassword;

	private String mRetrievedAccessToken;
	private String mRetrievedTokenType;
	private Integer mRetrievedExpiresIn;
	private String mRetrievedRefreshToken;

	private User mRetrievedUser;

	public String mErrorMessage;

	public LogInRequestHelper(Context context, String username, String password) {
		super(context);

		mSentUsername = username;
		mSentPassword = password;
	}

	public String getSentUsername() {
		return mSentUsername;
	}

	public String getSentPassword() {
		return mSentPassword;
	}

	public String getRetrievedAccessToken() {
		return mRetrievedAccessToken;
	}

	public String getRetrievedTokenType() {
		return mRetrievedTokenType;
	}

	public Integer getRetrievedExpiresIn() {
		return mRetrievedExpiresIn;
	}

	public String getRetrievedRefreshToken() {
		return mRetrievedRefreshToken;
	}

	public User getRetrievedUser() {
		return mRetrievedUser;
	}

	public String getErrorMessage() {
		return mErrorMessage;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = OAUTH_BASE_URI.buildUpon()
				.appendPath(PATH_OAUTH)
				.appendPath(PATH_TOKEN)
				.appendQueryParameter("client_id", Config.APP_ID)
				.appendQueryParameter("client_secret", Config.APP_SECRET)
				.appendQueryParameter("grant_type", "password")
				.appendQueryParameter("username", mSentUsername)
				.appendQueryParameter("password", mSentPassword)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
	}

	@Override
	protected void setEntity() {
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, InvalidResourceOwnerException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				mRetrievedAccessToken = obj.get("access_token").toString();
				mRetrievedTokenType = obj.get("token_type").toString();
				mRetrievedExpiresIn = Integer.valueOf(obj.get("expires_in").toString());
				mRetrievedRefreshToken = obj.get("refresh_token").toString();

			} catch (NullPointerException | ParseException | org.json.simple.parser.ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			JSONParser parser = new JSONParser();
			JSONObject obj;
			try {
				obj = (JSONObject) parser.parse(responseBody);
			} catch (org.json.simple.parser.ParseException e) {
				throw new SystemException(e.getMessage());
			}
			String error;
			String errorMessage;
			try {
				error = obj.get("error").toString();
				errorMessage = obj.get("error_description").toString();
			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			}
			if (!TextUtils.isEmpty(error)) {
				if (error.equalsIgnoreCase("invalid_resource_owner")) {
					mErrorMessage = errorMessage;
					throw new InvalidResourceOwnerException(errorMessage);
				} else {
					throw new SystemException(errorMessage);
				}
			} else {
				throw new SystemException("");
			}
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public boolean requiresAuthentication() {
		return false;
	}
}
