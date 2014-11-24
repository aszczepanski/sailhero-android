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
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.model.Alert;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

public class CreateAlertRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String CREATE_ALERT_REQUEST_PATH = "alerts";

	private Alert mSentAlert;
	private Alert mRetrievedAlert;

	public CreateAlertRequestHelper(Context context, String alertType, Location location, String additionalInfo) {
		super(context);

		mContext = context;

		mSentAlert = new Alert();
		mSentAlert.setAlertType(alertType);
		mSentAlert.setLocation(location);
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
		final String apiHost = Config.API_HOST;
		final String apiPath = Config.API_PATH;
		final String version = Config.VERSION;
		final String i18n = Config.I18N;

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(CREATE_ALERT_REQUEST_PATH)
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

				JSONObject alertObject = (JSONObject) obj.get("alert");
				mRetrievedAlert = new Alert(alertObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
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
