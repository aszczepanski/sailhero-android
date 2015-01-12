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

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.User;
import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;

public class RefreshTokenRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private static final String PATH_OAUTH = "oauth";
	private static final String PATH_TOKEN = "token";

	private String mSentAccessToken;
	private String mSentRefreshToken;

	private String mRetrievedAccessToken;
	private String mRetrievedTokenType;
	private Integer mRetrievedExpiresIn;
	private String mRetrievedRefreshToken;

	private User mRetrievedUser;

	public String mErrorMessage;

	public RefreshTokenRequestHelper(Context context, String accessToken, String refreshToken) {
		super(context);

		mSentAccessToken = accessToken;
		mSentRefreshToken = refreshToken;
	}

	public String getSentAccessToken() {
		return mSentAccessToken;
	}

	public String getSentRefreshToken() {
		return mSentRefreshToken;
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
		Uri uri = OAUTH_BASE_URI.buildUpon().appendPath(PATH_OAUTH).appendPath(PATH_TOKEN).build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		mHttpUriRequest.addHeader("Authorization", "Bearer " + mSentAccessToken);
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();
		obj.put("grant_type", "refresh_token");
		obj.put("refresh_token", mSentRefreshToken);

		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString(), CHARSET);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		((HttpPost) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException {
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
				if (error.equalsIgnoreCase("invalid_grant")) {
					mErrorMessage = errorMessage;
					throw new UnauthorizedException(errorMessage);
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
}
