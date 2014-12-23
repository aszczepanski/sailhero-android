package put.sailhero.service;

import java.util.concurrent.atomic.AtomicBoolean;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.ThrottledContentObserver;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class AlertService extends Service implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	public static final int ALERT_NOTIFICATION_ID = 1;

	private static final long UPDATE_INTERVAL = 5000;
	private static final long FASTEST_INTERVAL = 2000;

	private static LocationClient mLocationClient;
	private static LocationRequest mLocationRequest;

	private AlertServiceListener mAlertServiceListener;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder mNotificationBuilder;

	private ThrottledContentObserver mAlertsObserver;

	private final IBinder mBinder = new LocalBinder();

	private AtomicBoolean mIsServiceRunning;

	public class LocalBinder extends Binder {
		public AlertService getService() {
			// Return this instance of AlertService so clients can call public methods
			return AlertService.this;
		}
	}

	public void registerListener(AlertServiceListener listener) {
		//		assert mAlertServiceListener == null;
		if (mAlertServiceListener == null) {
			mAlertServiceListener = listener;
		}

		if (mAlertServiceListener != null) {
			// mNotificationManager.cancel(ALERT_NOTIFICATION_ID);
		}
	}

	public void unregisterListener(AlertServiceListener listener) {
		if (mAlertServiceListener == listener) {
			mAlertServiceListener = null;
		}
	}

	public static interface AlertServiceListener {
		void onClosestAlertUpdate(Location currentLocation, Alert alert);

		void onClosestAlertToRespondUpdate(Location currentLocation, Alert alert);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(Config.TAG, "AlertService onCreate()");

		PrefUtils.setAlertToRespond(this, null);
		PrefUtils.setLastKnownLocation(this, null);

		mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		mNotificationBuilder = new NotificationCompat.Builder(this);
		mNotificationBuilder.setSmallIcon(R.drawable.ic_launcher);
		mNotificationBuilder.setContentTitle("SailHero is running");
		mNotificationManager.notify(0x01, mNotificationBuilder.build());

		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

		mLocationClient.connect();

		Uri uri = SailHeroContract.Alert.CONTENT_URI;
		mAlertsObserver = new ThrottledContentObserver(new ThrottledContentObserver.Callbacks() {
			@Override
			public void onThrottledContentObserverFired() {
				Log.i(Config.TAG, "AlertService: requesting alerts refresh");
				refreshAlertData(null);
			}
		});
		getContentResolver().registerContentObserver(uri, true, mAlertsObserver);

		mIsServiceRunning.set(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.e(Config.TAG, "AlertService onDestroy()");

		mIsServiceRunning.set(false);

		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();

		getContentResolver().unregisterContentObserver(mAlertsObserver);

		mNotificationManager.cancel(ALERT_NOTIFICATION_ID);

		PrefUtils.setAlertToRespond(this, null);
		PrefUtils.setLastKnownLocation(this, null);
	}

	public AlertService() {
		Log.d(Config.TAG, "AlertService()");
		mIsServiceRunning = new AtomicBoolean(false);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
	}

	@Override
	public void onLocationChanged(Location location) {
		Log.d(Config.TAG, location.getLatitude() + " " + location.getLongitude());

		PrefUtils.setLastKnownLocation(this, location);

		refreshAlertData(location);
	}

	public void refreshAlertData(Location location) {
		if (mIsServiceRunning.get() == false) {
			return;
		}

		if (location == null) {
			if (mLocationClient.isConnected()) {
				location = mLocationClient.getLastLocation();
			} else {
				Location lastKnownLocation = PrefUtils.getLastKnownLocation(AlertService.this);
				if (lastKnownLocation != null) {
					location = lastKnownLocation;
				} else {
					Log.e(Config.TAG, "cannot refresh alert data, unknown location");
					return;
				}
			}
		}

		User currentUser = PrefUtils.getUser(this);

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

		if (mAlertServiceListener != null) {
			mAlertServiceListener.onClosestAlertUpdate(location, closestAlert);
			mAlertServiceListener.onClosestAlertToRespondUpdate(location, closestAlertToRespond);
		}

		if (closestAlert != null && closestAlertDistance <= PrefUtils.getAlertRadius(this)) {
			Log.w(Config.TAG, "building notification");

			mNotificationBuilder.setContentText("You are " + Math.round(closestAlertDistance) + "m from "
					+ closestAlert.getAlertType());

			mNotificationManager.notify(ALERT_NOTIFICATION_ID, mNotificationBuilder.build());
		} else {
			mNotificationBuilder.setContentText("");
			mNotificationManager.notify(ALERT_NOTIFICATION_ID, mNotificationBuilder.build());
			// mNotificationManager.cancel(ALERT_NOTIFICATION_ID);
		}

		if (closestAlertToRespond != null) {
		}

		PrefUtils.setAlertToRespond(this, closestAlertToRespond);
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
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
