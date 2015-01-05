package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.sync.CreateAlertRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.SyncUtils;
import put.sailhero.util.UnitUtils;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;

public class DashboardActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private Context mContext;

	private LocationClient mLocationClient;

	private TextView mSpeedTextView;
	private TextView mSpeedUnitTextView;
	private TextView mLatitudeTextView;
	private TextView mLongitudeTextView;

	private TextView mAlertTextView;
	private TextView mAlertDistanceTextView;
	private TextView mAlertDistanceUnitTextView;

	private ImageView mArrowImageView;

	private Button mBadWeatherConditionsButton;
	private Button mClosedAreaButton;
	private Button mYachtFailureButton;

	private String[] alertTypesArray;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		Log.i(TAG, "MainActivity::onCreate");

		mContext = DashboardActivity.this;

		mLocationClient = new LocationClient(mContext, this, this);

		mSpeedTextView = (TextView) findViewById(R.id.speed_text_view);
		mSpeedUnitTextView = (TextView) findViewById(R.id.speed_unit_text_view);
		mLatitudeTextView = (TextView) findViewById(R.id.latitude_text_view);
		mLongitudeTextView = (TextView) findViewById(R.id.longitude_text_view);

		mAlertTextView = (TextView) findViewById(R.id.alert_text_view);
		mAlertDistanceTextView = (TextView) findViewById(R.id.alert_distance_text_view);
		mAlertDistanceUnitTextView = (TextView) findViewById(R.id.alert_distance_unit_text_view);

		mArrowImageView = (ImageView) findViewById(R.id.arrow_image);

		Typeface font = Typeface.createFromAsset(getAssets(), "fontawesome-webfont.ttf");

		mBadWeatherConditionsButton = (Button) findViewById(R.id.bad_weather_conditions_button);
		mBadWeatherConditionsButton.setTypeface(font);
		mBadWeatherConditionsButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("BAD_WEATHER_CONDITIONS");
			}
		});

		mClosedAreaButton = (Button) findViewById(R.id.closed_area_button);
		mClosedAreaButton.setTypeface(font);
		mClosedAreaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("CLOSED_AREA");
			}
		});

		mYachtFailureButton = (Button) findViewById(R.id.yacht_failure_button);
		mYachtFailureButton.setTypeface(font);
		mYachtFailureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert("YACHT_FAILURE");
			}
		});

		overridePendingTransition(0, 0);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "Using: " + account.name, Toast.LENGTH_SHORT).show();
		}

		alertTypesArray = getResources().getStringArray(R.array.alert_names);
	}

	private void submitAlert(String alertType) {
		if (!mLocationClient.isConnected()) {
			Toast.makeText(mContext, "Location services are not connected.", Toast.LENGTH_SHORT).show();
			return;
		}

		final Location currentLocation = mLocationClient.getLastLocation();

		final CreateAlertRequestHelper createAlertRequestHelper = new CreateAlertRequestHelper(mContext, alertType,
				currentLocation.getLatitude(), currentLocation.getLongitude(), "");
		RequestHelperAsyncTask createAlertTask = new RequestHelperAsyncTask(mContext, createAlertRequestHelper,
				new RequestHelperAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(RequestHelper requestHelper) {
						Log.d(TAG, "created alert with id " + createAlertRequestHelper.getRetrievedAlert().getId());
					}
				});
		createAlertTask.execute();
	}

	private String getStringForAlertType(String alertType) {
		if (alertType.equals("CLOSED_AREA")) {
			return "Closed area";
		} else if (alertType.equals("BAD_WEATHER_CONDITIONS")) {
			return "Bad weather conditions";
		} else if (alertType.equals("YACHT_FAILURE")) {
			return "Yacht failure";
		} else {
			return "N/A";
		}
	}

	@Override
	protected void onClosestAlertUpdate(Location currentLocation, Alert alert) {
		if (currentLocation != null) {
			UnitUtils.DegMinSec latitudeDegMinSec = UnitUtils.decimalToDegMinSec(currentLocation.getLatitude());
			mLatitudeTextView.setText(String.format("%02d\u00B0%02d\u2032%02d\u2033", latitudeDegMinSec.getDegrees(),
					latitudeDegMinSec.getMinutes(), latitudeDegMinSec.getSeconds()));

			UnitUtils.DegMinSec longitudeDegMinSec = UnitUtils.decimalToDegMinSec(currentLocation.getLongitude());
			mLongitudeTextView.setText(String.format("%02d\u00B0%02d\u2032%02d\u2033", longitudeDegMinSec.getDegrees(),
					longitudeDegMinSec.getMinutes(), longitudeDegMinSec.getSeconds()));

			if (currentLocation.hasSpeed()) {
				mSpeedTextView.setText(UnitUtils.roundSpeedToHalf(currentLocation.getSpeed()).toString());
				mSpeedUnitTextView.setVisibility(View.VISIBLE);

			} else {
				mSpeedTextView.setText("N/A");
				mSpeedUnitTextView.setVisibility(View.GONE);
			}
		} else {
			mSpeedTextView.setText("N/A");
			mSpeedUnitTextView.setVisibility(View.GONE);
			mLatitudeTextView.setText("N/A");
			mLongitudeTextView.setText("N/A");
		}

		if (alert != null) {
			Integer distanceToAlert = UnitUtils.roundDistanceTo25(currentLocation.distanceTo(alert.getLocation()));

			mAlertTextView.setText(getStringForAlertType(alert.getAlertType()));
			mAlertDistanceTextView.setText(distanceToAlert.toString());
			mAlertDistanceUnitTextView.setVisibility(View.VISIBLE);

			if (currentLocation.hasBearing() && distanceToAlert > 0) {
				float currentBearing = currentLocation.getBearing();
				float bearingToAlert = currentLocation.bearingTo(alert.getLocation());

				mArrowImageView.setRotation(bearingToAlert - currentBearing);
				mArrowImageView.setVisibility(View.VISIBLE);
			} else {
				mArrowImageView.setVisibility(View.INVISIBLE);
			}
		} else {
			mAlertTextView.setText("N/A");
			mAlertDistanceTextView.setText("N/A");
			mAlertDistanceUnitTextView.setVisibility(View.GONE);
			mArrowImageView.setVisibility(View.INVISIBLE);
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
