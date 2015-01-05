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

		Cursor cursor = getContentResolver().query(SailHeroContract.Alert.CONTENT_URI, Alert.Query.PROJECTION, null,
				null, null);

		float closestAlertDistance = Float.MAX_VALUE;
		float closestAlertToRespondDistance = Float.MAX_VALUE;
		Alert closestAlert = null;
		Alert closestAlertToRespond = null;

		while (cursor.moveToNext()) {
			Alert alert = new Alert(cursor);

			float distanceToAlert = location.distanceTo(alert.getLocation());
			if (distanceToAlert < closestAlertDistance) {
				closestAlertDistance = distanceToAlert;
				closestAlert = alert;
			}

			if (distanceToAlert < closestAlertToRespondDistance && currentUser != null
					&& currentUser.getId() != alert.getUserId() && !alert.hasUserResponded()) {
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
}
