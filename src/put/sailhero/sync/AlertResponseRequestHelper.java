package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Alert;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;

public abstract class AlertResponseRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	protected final static String PATH_ALERTS = "alerts";

	protected Integer mSentId;
	protected Alert mRetrievedAlert;

	protected Boolean mRetrievedAlertIsActive;

	public AlertResponseRequestHelper(Context context, Integer alertId) {
		super(context);

		mSentId = alertId;
	}

	public Integer getSentId() {
		return mSentId;
	}

	public Alert getRetrievedAlert() {
		return mRetrievedAlert;
	}

	public Boolean getRetrievedAlertIsActive() {
		return mRetrievedAlertIsActive;
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, ForbiddenException,
			NotFoundException, InvalidRegionException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONObject alertObject = (JSONObject) obj.get("alert");
				mRetrievedAlert = new Alert(alertObject);
				mRetrievedAlertIsActive = (Boolean) alertObject.get("active");

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 403) {
			throw new ForbiddenException();
		} else if (statusCode == 404) {
			throw new NotFoundException();
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else {
			throw new SystemException("Invalid status code(" + statusCode + ")");
		}
	}

	@Override
	public void storeData() {
		if (mRetrievedAlert == null) {
			return;
		}

		Uri alertUri = SailHeroContract.Alert.CONTENT_URI.buildUpon()
				.appendPath(mRetrievedAlert.getId().toString())
				.build();

		if (mRetrievedAlertIsActive) {
			ContentValues values = new ContentValues();
			values.put(SailHeroContract.Alert.COLUMN_NAME_USER_RESPONDED,
					SailHeroContract.Alert.RESPONSE_STATUS_RESPONDED);

			mContext.getContentResolver().update(alertUri, values, null, null);
		} else {
			mContext.getContentResolver().delete(alertUri, null, null);
		}
	}
}
