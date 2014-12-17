package put.sailhero.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Alert;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrieveAlertsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_ALERTS = "alerts";

	private LinkedList<Alert> mAlerts;

	public RetrieveAlertsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendEncodedPath(PATH_ALERTS).build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, InvalidRegionException, SystemException {
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

				LinkedList<Alert> alerts = new LinkedList<Alert>();

				JSONArray alertsArray = (JSONArray) obj.get("alerts");
				for (int i = 0; i < alertsArray.size(); i++) {
					JSONObject alertObject = (JSONObject) alertsArray.get(i);
					Alert alert = new Alert(alertObject);

					alerts.addLast(alert);
					// Log.i(TAG, alert.getId().toString());
					Log.i(TAG, alertObject.toString());
				}

				mAlerts = alerts;

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
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}

	@Override
	public void storeData() throws SystemException {
		ContentResolver contentResolver = mContext.getContentResolver();

		// Build hash table of incoming alerts
		HashMap<Integer, Alert> alertMap = new HashMap<Integer, Alert>();
		for (Alert alert : mAlerts) {
			Log.i(TAG, alert.getId() + " " + alert.getAlertType());
			alertMap.put(alert.getId(), alert);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		Log.i(TAG, SailHeroContract.Alert.CONTENT_URI.toString());

		String[] projection = new String[] {
				SailHeroContract.Alert.COLUMN_NAME_ID,
				SailHeroContract.Alert.COLUMN_NAME_TYPE
		};

		int id;

		Cursor c = contentResolver.query(SailHeroContract.Alert.CONTENT_URI, projection, null, null, null);

		while (c.moveToNext()) {
			Log.i(TAG, c.getInt(0) + " " + c.getString(1));
			id = c.getInt(0);

			Alert alert = alertMap.get(id);
			if (alert != null) {
				// alert already in database
				alertMap.remove(id);
			} else {
				// alert should be deleted from database
				Uri deleteUri = SailHeroContract.Alert.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Alert alert : alertMap.values()) {
			Log.i(TAG, "Scheduling insert: alert_id=" + alert.getId());
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Alert.CONTENT_URI)
					.withValue(SailHeroContract.Alert.COLUMN_NAME_ID, alert.getId())
					.withValue(SailHeroContract.Alert.COLUMN_NAME_TYPE, alert.getAlertType())
					.withValue(SailHeroContract.Alert.COLUMN_NAME_LATITUDE, alert.getLocation().getLatitude())
					.withValue(SailHeroContract.Alert.COLUMN_NAME_LONGITUDE, alert.getLocation().getLongitude())
					.withValue(SailHeroContract.Alert.COLUMN_NAME_USER_ID, alert.getUserId())
					.withValue(SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO, alert.getAdditionalInfo())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
