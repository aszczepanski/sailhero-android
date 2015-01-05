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

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Alert;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class CreateAlertRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_ALERTS = "alerts";

	private Alert mSentAlert;
	private Alert mRetrievedAlert;

	public CreateAlertRequestHelper(Context context, String alertType, Double latitude, Double longitude,
			String additionalInfo) {
		super(context);

		mContext = context;

		mSentAlert = new Alert();
		mSentAlert.setAlertType(alertType);
		mSentAlert.setLatitude(latitude);
		mSentAlert.setLongitude(longitude);
		mSentAlert.setAdditionalInfo(additionalInfo);
	}

	public Alert getSentAlert() {
		return mSentAlert;
	}

	public Alert getRetrievedAlert() {
		return mRetrievedAlert;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_ALERTS).build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();

		JSONObject alertObject = mSentAlert.toJSONObject();
		obj.put("alert", alertObject);

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
	protected void parseResponse() throws SystemException, UnauthorizedException, InvalidRegionException {
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

				JSONObject alertObject = (JSONObject) obj.get("alert");
				mRetrievedAlert = new Alert(alertObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else {
			throw new SystemException("Invalid status code(" + statusCode + ")");
		}
	}

	@Override
	public void storeData() {
		Log.i(TAG, "Scheduling insert: alert_id=" + mRetrievedAlert.getId());
		ContentValues values = mRetrievedAlert.toContentValues();
		mContext.getContentResolver().insert(SailHeroContract.Alert.CONTENT_URI, values);
	}
}
