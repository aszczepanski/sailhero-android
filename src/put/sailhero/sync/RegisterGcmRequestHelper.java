package put.sailhero.sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

public class RegisterGcmRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_USERS = "users";
	private final static String PATH_ME = "me";
	private final static String PATH_DEVICES = "devices";

	private String mRegistrationId;

	public RegisterGcmRequestHelper(Context context, String registrationId) {
		super(context);

		mRegistrationId = registrationId;
	}

	public String getRegistrationId() {
		return mRegistrationId;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_USERS).appendPath(PATH_ME).appendPath(PATH_DEVICES).build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderContentJson();
	}

	private String getDeviceName() {
		return Build.BRAND + " " + Build.MODEL;
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();

		obj.put("device_type", "ANDROID");
		obj.put("name", getDeviceName());
		obj.put("key", mRegistrationId);

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

		Log.e(TAG, responseBody);

		if (statusCode == 201) {
			// gcm id registered
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 422) {
			throw new UnprocessableEntityException(responseBody);
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() {
		// TODO: save using content resolver
	}
}
