package put.sailhero.service;

import put.sailhero.Config;
import put.sailhero.model.Alert;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import android.app.IntentService;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.util.Log;

public class AlertIntentService extends IntentService {
	public AlertIntentService() {
		super("AlertIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.i(Config.TAG, "AlertIntentService::onHandleIntent");

		User currentUser = PrefUtils.getUser(this);
		Location location = PrefUtils.getLastKnownLocation(this);

		Cursor cursor = getContentResolver().query(SailHeroContract.Alert.CONTENT_URI, AlertQuery.PROJECTION, null,
				null, null);

		float closestAlertDistance = Float.MAX_VALUE;
		float closestAlertToRespondDistance = Float.MAX_VALUE;
		Alert closestAlert = null;
		Alert closestAlertToRespond = null;

		while (cursor.moveToNext()) {
			Alert alert = new Alert();
			alert.setId(cursor.getInt(AlertQuery.ALERT_ID));
			alert.setAlertType(cursor.getString(AlertQuery.ALERT_TYPE));

			alert.setLatitude(cursor.getDouble(AlertQuery.ALERT_LATITUDE));
			alert.setLongitude(cursor.getDouble(AlertQuery.ALERT_LONGITUDE));

			alert.setUserId(cursor.getInt(AlertQuery.ALERT_USER_ID));
			alert.setAdditionalInfo(cursor.getString(AlertQuery.ALERT_ADDITIONAL_INFO));

			float distanceToAlert = location.distanceTo(alert.getLocation());
			if (distanceToAlert < closestAlertDistance) {
				closestAlertDistance = distanceToAlert;
				closestAlert = alert;
			}

			int responseStatus = cursor.getInt(AlertQuery.ALERT_RESPONSE_STATUS);

			if (distanceToAlert < closestAlertToRespondDistance && currentUser != null
					&& currentUser.getId() != alert.getUserId()
					&& responseStatus == SailHeroContract.Alert.RESPONSE_STATUS_NOT_RESPONDED) {
				closestAlertToRespondDistance = distanceToAlert;
				closestAlertToRespond = alert;
			}
		}

		cursor.close();

		Log.d(Config.TAG, closestAlert != null ? closestAlert.getAlertType() : "null");
		Log.d(Config.TAG, closestAlertToRespond != null ? closestAlertToRespond.getAlertType() : "null");

		PrefUtils.setClosestAlert(this, closestAlert);
		PrefUtils.setClosestAlertToRespond(this, closestAlertToRespond);
	}

	@Override
	public void onDestroy() {
		Log.i(Config.TAG, "AlertIntentService::onDestroy");
		super.onDestroy();
	}

	private interface AlertQuery {
		String[] PROJECTION = {
				SailHeroContract.Alert.COLUMN_NAME_ID,
				SailHeroContract.Alert.COLUMN_NAME_TYPE,
				SailHeroContract.Alert.COLUMN_NAME_LATITUDE,
				SailHeroContract.Alert.COLUMN_NAME_LONGITUDE,
				SailHeroContract.Alert.COLUMN_NAME_USER_ID,
				SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO,
				SailHeroContract.Alert.COLUMN_NAME_RESPONSE_STATUS
		};

		int ALERT_ID = 0;
		int ALERT_TYPE = 1;
		int ALERT_LATITUDE = 2;
		int ALERT_LONGITUDE = 3;
		int ALERT_USER_ID = 4;
		int ALERT_ADDITIONAL_INFO = 5;
		int ALERT_RESPONSE_STATUS = 6;
	}
}
