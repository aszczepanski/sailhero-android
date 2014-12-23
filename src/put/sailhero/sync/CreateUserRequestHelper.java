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
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.model.User;
import put.sailhero.ui.RegisterUserEntityErrorsHolder;
import android.content.Context;
import android.net.Uri;

public class CreateUserRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_USERS = "users";

	private String mEmail, mPassword, mPasswordConfirmation, mName, mSurname;

	private RegisterUserEntityErrorsHolder mRegisterUserEntityErrorsHolder;

	private User mRetrievedUser;

	public CreateUserRequestHelper(Context context, String email, String password, String passwordConfirmation,
			String name, String surname) {
		super(context);

		mEmail = email;
		mPassword = password;
		mPasswordConfirmation = passwordConfirmation;
		mName = name;
		mSurname = surname;
	}

	public String getEmail() {
		return mEmail;
	}

	public String getPassword() {
		return mPassword;
	}

	public String getPasswordConfirmation() {
		return mPasswordConfirmation;
	}

	public String getName() {
		return mName;
	}

	public String getSurname() {
		return mSurname;
	}

	public User getRetrievedUser() {
		return mRetrievedUser;
	}

	public RegisterUserEntityErrorsHolder getRegisterUserEntityErrorsHolder() {
		return mRegisterUserEntityErrorsHolder;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_USERS).build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();

		JSONObject userObject = new JSONObject();
		userObject.put("email", mEmail);
		userObject.put("password", mPassword);
		userObject.put("password_confirmation", mPasswordConfirmation);
		userObject.put("name", mName);
		userObject.put("surname", mSurname);

		obj.put("user", userObject);

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
	protected void parseResponse() throws SystemException, UnauthorizedException, UnprocessableEntityException {
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

				JSONObject userObject = (JSONObject) obj.get("user");
				mRetrievedUser = new User(userObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 422) {
			mRegisterUserEntityErrorsHolder = new RegisterUserEntityErrorsHolder(responseBody);
			throw new UnprocessableEntityException(responseBody);
		} else {
			throw new SystemException("Invalid status code");
		}
	}
	
	@Override
	public boolean requiresAuthentication() {
		return false;
	}
}
