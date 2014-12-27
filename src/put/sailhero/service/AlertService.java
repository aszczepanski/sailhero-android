package put.sailhero.service;

import java.util.concurrent.atomic.AtomicBoolean;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.ThrottledContentObserver;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
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

	@Override
	public void onCreate() {
		super.onCreate();
		Log.e(Config.TAG, "AlertService onCreate()");

		PrefUtils.setClosestAlert(this, null);
		PrefUtils.setClosestAlertToRespond(this, null);
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

		PrefUtils.setClosestAlert(this, null);
		PrefUtils.setClosestAlertToRespond(this, null);
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
			Log.e(Config.TAG, "new location is null");
			return;
		}

		Intent intent = new Intent(AlertService.this, AlertIntentService.class);
		startService(intent);

		// TODO: implement SharedPreferences.OnSharedPreferenceChangeListener

		//		if (closestAlert != null && closestAlertDistance <= PrefUtils.getAlertRadius(this)) {
		//			Log.w(Config.TAG, "building notification");
		//
		//			mNotificationBuilder.setContentText("You are " + Math.round(closestAlertDistance) + "m from "
		//					+ closestAlert.getAlertType());
		//
		//			mNotificationManager.notify(ALERT_NOTIFICATION_ID, mNotificationBuilder.build());
		//		} else {
		//			mNotificationBuilder.setContentText("");
		//			mNotificationManager.notify(ALERT_NOTIFICATION_ID, mNotificationBuilder.build());
		//		}
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

}
