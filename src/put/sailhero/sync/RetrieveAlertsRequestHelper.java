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
import android.content.ContentValues;
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
		addHeaderPosition();
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

		Log.e(TAG, responseBody);
		
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

		Cursor c = contentResolver.query(SailHeroContract.Alert.CONTENT_URI, Alert.Query.PROJECTION, null, null, null);

		while (c.moveToNext()) {
			Alert dbAlert = new Alert(c);
			Log.i(TAG, dbAlert.getId() + " " + dbAlert.getAlertType());

			Alert retrievedAlert = alertMap.get(dbAlert.getId());
			if (retrievedAlert != null) {
				// alert already in database
				if (dbAlert.equals(retrievedAlert)) {
					// do nothing
				} else {
					Uri updateUri = SailHeroContract.Alert.CONTENT_URI.buildUpon()
							.appendPath(Integer.toString(dbAlert.getId()))
							.build();
					Log.i(TAG, "Scheduling update: " + updateUri);

					ContentValues values = retrievedAlert.toContentValues();
					batch.add(ContentProviderOperation.newUpdate(updateUri).withValues(values).build());
				}
				alertMap.remove(dbAlert.getId());
			} else {
				// alert should be deleted from database
				Uri deleteUri = SailHeroContract.Alert.CONTENT_URI.buildUpon()
						.appendPath(Integer.toString(dbAlert.getId()))
						.build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Alert retrievedAlert : alertMap.values()) {
			Log.i(TAG, "Scheduling insert: alert_id=" + retrievedAlert.getId());
			ContentValues values = retrievedAlert.toContentValues();
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Alert.CONTENT_URI).withValues(values).build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
