package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.sync.CreateAlertRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.StringUtils;
import put.sailhero.util.SyncUtils;
import put.sailhero.util.UnitUtils;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class DashboardActivity extends BaseActivity {

	private Context mContext;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		if (!isTaskRoot()) {
			final Intent intent = getIntent();
			final String intentAction = intent.getAction();
			if (intent.hasCategory(Intent.CATEGORY_LAUNCHER) && intentAction != null
					&& intentAction.equals(Intent.ACTION_MAIN)) {
				Log.w(TAG, "Dashboard Activity is not the root.  Finishing Dashboard Activity instead of launching.");
				finish();
				return;
			}
		}

		Log.i(TAG, "MainActivity::onCreate");

		mContext = DashboardActivity.this;

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
				submitAlert(StringUtils.ALERT_TYPE_BAD_WEATHER_CONDITIONS);
			}
		});

		mClosedAreaButton = (Button) findViewById(R.id.closed_area_button);
		mClosedAreaButton.setTypeface(font);
		mClosedAreaButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert(StringUtils.ALERT_TYPE_CLOSED_AREA);
			}
		});

		mYachtFailureButton = (Button) findViewById(R.id.yacht_failure_button);
		mYachtFailureButton.setTypeface(font);
		mYachtFailureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				submitAlert(StringUtils.ALERT_TYPE_YACHT_FAILURE);
			}
		});

		overridePendingTransition(0, 0);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "Using: " + account.name, Toast.LENGTH_SHORT).show();
		}

		SyncUtils.syncRegions(this);
	}

	private void submitAlert(String alertType) {
		final Location currentLocation = PrefUtils.getLastKnownLocation(mContext);
		if (currentLocation == null) {
			Toast.makeText(mContext, "Location data is not ready.", Toast.LENGTH_SHORT).show();
			return;
		}

		final CreateAlertRequestHelper createAlertRequestHelper = new CreateAlertRequestHelper(mContext, alertType,
				currentLocation.getLatitude(), currentLocation.getLongitude(), "");
		RequestHelperAsyncTask createAlertTask = new RequestHelperAsyncTask(mContext, "Creating alert",
				StringUtils.getStringForAlertType(mContext, alertType), createAlertRequestHelper,
				new RequestHelperAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(RequestHelper requestHelper) {
						Toast.makeText(mContext, "Alert created.", Toast.LENGTH_SHORT).show();
						Log.d(TAG, "created alert with id " + createAlertRequestHelper.getRetrievedAlert().getId());
					}

					@Override
					public void onInvalidRegionException(RequestHelper requestHelper) {
						Toast.makeText(mContext, "Choose a region first.", Toast.LENGTH_SHORT).show();
					}
				});
		createAlertTask.execute();
	}

	@Override
	protected void onLastKnownLocationUpdate(Location location) {
		super.onLastKnownLocationUpdate(location);

		if (location != null) {
			UnitUtils.DegMinSec latitudeDegMinSec = UnitUtils.decimalToDegMinSec(location.getLatitude());
			mLatitudeTextView.setText(String.format("%02d\u00B0%02d\u2032%02d\u2033", latitudeDegMinSec.getDegrees(),
					latitudeDegMinSec.getMinutes(), latitudeDegMinSec.getSeconds()));

			UnitUtils.DegMinSec longitudeDegMinSec = UnitUtils.decimalToDegMinSec(location.getLongitude());
			mLongitudeTextView.setText(String.format("%02d\u00B0%02d\u2032%02d\u2033", longitudeDegMinSec.getDegrees(),
					longitudeDegMinSec.getMinutes(), longitudeDegMinSec.getSeconds()));

			if (location.hasSpeed()) {
				mSpeedTextView.setText(UnitUtils.roundSpeedToHalf(location.getSpeed()).toString());
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

		Alert closestAlert = PrefUtils.getClosestAlert(this);
		updateAlertBox(location, closestAlert);
	}

	@Override
	protected void onClosestAlertUpdate(Alert alert) {
		super.onClosestAlertUpdate(alert);

		Location currentLocation = PrefUtils.getLastKnownLocation(DashboardActivity.this);

		updateAlertBox(currentLocation, alert);
	}

	private void updateAlertBox(Location location, Alert alert) {
		if (alert != null) {
			mAlertTextView.setText(StringUtils.getStringForAlertType(DashboardActivity.this, alert.getAlertType()));

			if (location != null) {
				Integer distanceToAlert = UnitUtils.roundDistanceTo25(location.distanceTo(alert.getLocation()));

				mAlertDistanceTextView.setText(distanceToAlert.toString());
				mAlertDistanceUnitTextView.setVisibility(View.VISIBLE);

				if (location.hasBearing() && distanceToAlert > 0) {
					float currentBearing = location.getBearing();
					float bearingToAlert = location.bearingTo(alert.getLocation());

					mArrowImageView.setRotation(bearingToAlert - currentBearing);
					mArrowImageView.setVisibility(View.VISIBLE);
				} else {
					mArrowImageView.setVisibility(View.INVISIBLE);
				}
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
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "MainActivity::onSaveInstanceState");

		super.onSaveInstanceState(outState);
	}

	@Override
	protected void requestDataRefresh() {
		super.requestDataRefresh();

		SyncUtils.syncAlerts(this);
		SyncUtils.syncUserData(this);
		SyncUtils.syncRegions(this);
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
		if (id == R.id.action_finish) {
			finish();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
