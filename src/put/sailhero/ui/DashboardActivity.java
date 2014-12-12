package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.sync.CreateAlertRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.SyncUtils;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class DashboardActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private Context mContext;

	private LocationClient mLocationClient;

	private SensorManager mSensorManager;
	private float[] mGData = new float[3];
	private float[] mMData = new float[3];
	private float[] mR = new float[16];
	private float[] mI = new float[16];
	private float[] mOrientation = new float[3];
	private int mCount;

	private Alert mClosestAlert;

	private TextView mLocationTextView;
	private TextView mBearingTextView;
	private TextView mSpeedTextView;
	private TextView mDirTextView;
	private TextView mAlertTextView;
	private TextView mAlertDistanceTextView;
	private TextView mAlertBearingTextView;

	private Button mBadWeatherConditionsButton;
	private Button mClosedAreaButton;
	private Button mYachtFailureButton;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		Log.i(TAG, "MainActivity::onCreate");

		mContext = DashboardActivity.this;

		mLocationClient = new LocationClient(mContext, this, this);

		mLocationTextView = (TextView) findViewById(R.id.location_text_view);
		mBearingTextView = (TextView) findViewById(R.id.bearing_text_view);
		mSpeedTextView = (TextView) findViewById(R.id.speed_text_view);
		mDirTextView = (TextView) findViewById(R.id.dir_text_view);
		mAlertTextView = (TextView) findViewById(R.id.alert_text_view);
		mAlertDistanceTextView = (TextView) findViewById(R.id.alert_distance_text_view);
		mAlertBearingTextView = (TextView) findViewById(R.id.alert_bearing_text_view);

		mBadWeatherConditionsButton = (Button) findViewById(R.id.bad_weather_conditions_button);
		mBadWeatherConditionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("BAD_WEATHER_CONDITIONS");
			}
		});

		mClosedAreaButton = (Button) findViewById(R.id.closed_area_button);
		mClosedAreaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("CLOSED_AREA");
			}
		});

		mYachtFailureButton = (Button) findViewById(R.id.yacht_failure_button);
		mYachtFailureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("YACHT_FAILURE");
			}
		});

		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

		overridePendingTransition(0, 0);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "Using: " + account.name, Toast.LENGTH_SHORT).show();
		}
	}

	private void submitAlert(String alertType) {
		if (!mLocationClient.isConnected()) {
			Toast.makeText(mContext, "Location services are not connected.", Toast.LENGTH_SHORT).show();
			return;
		}

		final Location currentLocation = mLocationClient.getLastLocation();

		final CreateAlertRequestHelper createAlertRequestHelper = new CreateAlertRequestHelper(mContext, alertType,
				currentLocation, "");
		RequestHelperAsyncTask createAlertTask = new RequestHelperAsyncTask(mContext, createAlertRequestHelper,
				new RequestHelperAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(RequestHelper requestHelper) {
						Log.d(TAG, "created alert with id " + createAlertRequestHelper.getRetrievedAlert().getId());
					}
				});
		createAlertTask.execute();
	}

	private final SensorEventListener mListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent event) {
			int type = event.sensor.getType();
			if (type == Sensor.TYPE_ACCELEROMETER) {
				mGData = event.values.clone();
			} else if (type == Sensor.TYPE_MAGNETIC_FIELD) {
				mMData = event.values.clone();
			} else {
				// we should not be here.
				return;
			}

			SensorManager.getRotationMatrix(mR, mI, mGData, mMData);

			// SensorManager.remapCoordinateSystem(mR, SensorManager.AXIS_X, SensorManager.AXIS_Z, mR);
			SensorManager.getOrientation(mR, mOrientation);
			float incl = SensorManager.getInclination(mI);

			if (mCount++ > 50) {
				final float rad2deg = (float) (180.0f / Math.PI);
				mCount = 0;
				mDirTextView.setText(("direction: " + (int) (mOrientation[0] * rad2deg)));
				// Log.d(Config.TAG, "yaw: " + (int) (mOrientation[0] * rad2deg) + "  pitch: "
				// + (int) (mOrientation[1] * rad2deg) + "  roll: " + (int) (mOrientation[2] * rad2deg)
				// + "  incl: " + (int) (incl * rad2deg));
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
		}
	};

	@Override
	protected void onClosestAlertUpdate(Location currentLocation, Alert alert) {
		if (currentLocation != null) {
			mLocationTextView.setText("location: " + currentLocation.getLatitude() + " "
					+ currentLocation.getLongitude());
			if (currentLocation.hasSpeed()) {
				mSpeedTextView.setText("speed: " + currentLocation.getSpeed());
			} else {
				mSpeedTextView.setText("speed: " + "N/A");
			}
			if (currentLocation.hasBearing()) {
				mBearingTextView.setText("bearing: " + currentLocation.getBearing());
			} else {
				mBearingTextView.setText("bearing: " + "N/A");
			}
		} else {
			mLocationTextView.setText("location: " + "N/A");
			mSpeedTextView.setText("speed: " + "N/A");
			mBearingTextView.setText("bearing: " + "N/A");
		}

		if (alert != null) {
			mAlertTextView.setText(alert.getId() + " " + alert.getAlertType());
			mAlertDistanceTextView.setText("distance: " + currentLocation.distanceTo(alert.getLocation()));

			mAlertBearingTextView.setText("bearing: " + currentLocation.bearingTo(alert.getLocation()));
		} else {
			mAlertTextView.setText("N/A");
			mAlertDistanceTextView.setText("distance: " + "N/A");
			mAlertBearingTextView.setText("bearing: " + "N/A");
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_DASHBOARD;
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Closing Activity")
				.setMessage("Are you sure you want to exit SailHero?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				})
				.setNegativeButton("No", null)
				.show();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "MainActivity::onStart");
		super.onStart();

		mLocationClient.connect();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "MainActivity::onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "MainActivity::onStop");
		super.onStop();

		mLocationClient.disconnect();
		mSensorManager.unregisterListener(mListener);
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "MainActivity::onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isFinishing()) {
			return;
		}

		SyncUtils.syncAll(mContext);

		Sensor gsensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		Sensor msensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(mListener, gsensor, SensorManager.SENSOR_DELAY_UI);
		mSensorManager.registerListener(mListener, msensor, SensorManager.SENSOR_DELAY_UI);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "MainActivity::onSaveInstanceState");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Toast.makeText(mContext, "Cannot connect to location services.", Toast.LENGTH_SHORT).show();
		Log.e(TAG, "Cannot connect to location services.");
	}

	@Override
	public void onConnected(Bundle bundle) {
	}

	@Override
	public void onDisconnected() {
		Toast.makeText(mContext, "Location services disconnected.", Toast.LENGTH_SHORT).show();
		Log.e(TAG, "Location services disconnected.");
	}
}
