package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.CreateAlertRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.ThrottledContentObserver;
import android.accounts.Account;
import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.content.SyncStatusObserver;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class AlertActivity extends BaseActivity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = "sailhero";

	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

	private static LocationClient mLocationClient;
	private static LocationRequest mLocationRequest;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new AlertFragment()).commit();
		}

		overridePendingTransition(0, 0);

		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
	}

	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_ALERT;
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		if (mLocationClient.isConnected()) {
			mLocationClient.removeLocationUpdates(this);
		}
		mLocationClient.disconnect();
		super.onStop();
	}

	public static class AlertFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

		final static String TAG = "sailhero";

		private Context mContext;

		private Account mAccount;

		private Object mSyncObserverHandle;

		private Spinner mSpinner;
		private Button mSubmitAlertButton;
		private Button mRefreshButton;

		private ThrottledContentObserver mAlertsObserver;

		private SimpleCursorAdapter mAdapter;

		private ListView mListView;

		public AlertFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_alert, container, false);

			mListView = (ListView) rootView.findViewById(R.id.listView1);

			mSpinner = (Spinner) rootView.findViewById(R.id.FragmentAlertSpinner);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.alert_names,
					android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(adapter);

			mSubmitAlertButton = (Button) rootView.findViewById(R.id.FragmentAlertSubmitAlertButton);
			mSubmitAlertButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String selectedAlert = mSpinner.getSelectedItem().toString();

					final Location currentLocation = mLocationClient.getLastLocation();

					final CreateAlertRequestHelper createAlertRequestHelper = new CreateAlertRequestHelper(mContext,
							getAlertTypeFromString(selectedAlert), currentLocation, "");
					RequestHelperAsyncTask createAlertTask = new RequestHelperAsyncTask(mContext,
							createAlertRequestHelper, new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Log.d(TAG, "created alert with id "
											+ createAlertRequestHelper.getRetrievedAlert().getId());
									// TODO: reload alerts from database
								}
							});
					createAlertTask.execute();

					Toast.makeText(getActivity(), "Selected alert: " + selectedAlert, Toast.LENGTH_SHORT).show();
				}
			});

			mRefreshButton = (Button) rootView.findViewById(R.id.refresh_button);
			mRefreshButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle bundle = new Bundle();
					// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
					bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
					bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
					ContentResolver.requestSync(mAccount, "put.sailhero", bundle);
				}
			});

			return rootView;
		}

		@Override
		public void onViewCreated(View view, Bundle savedInstanceState) {
			super.onViewCreated(view, savedInstanceState);

			mAdapter = new SimpleCursorAdapter(getActivity(), android.R.layout.simple_list_item_activated_2, null,
					new String[] {
							"_id",
							"type"
					}, new int[] {
							android.R.id.text1,
							android.R.id.text2
					}, 0);

			mAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor, int i) {
					return false;
				}
			});
			mListView.setAdapter(mAdapter);
		}

		private String getAlertTypeFromString(String alertType) {
			if (alertType.equals("Closed area")) {
				return "CLOSED_AREA";
			} else if (alertType.equals("Bad weather conditions")) {
				return "BAD_WEATHER_CONDITIONS";
			} else if (alertType.equals("Yacht failure")) {
				return "YACHT_FAILURE";
			} else {
				return "";
			}
		}

		private void onAlertsChanged() {
			Toast.makeText(mContext, "onAlertsChanged()", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "onAlertsChanged()");

			getLoaderManager().restartLoader(1, null, AlertFragment.this);
		}

		@Override
		public void onStart() {
			super.onStart();
		}

		@Override
		public void onStop() {
			super.onStop();
		}

		@Override
		public void onResume() {
			super.onResume();

			mAccount = AccountUtils.getActiveAccount(mContext);

			Uri uri = Uri.parse("content://put.sailhero").buildUpon().appendPath("alerts").build();
			mAlertsObserver = new ThrottledContentObserver(new ThrottledContentObserver.Callbacks() {
				@Override
				public void onThrottledContentObserverFired() {
					onAlertsChanged();
				}
			});
			getActivity().getContentResolver().registerContentObserver(uri, true, mAlertsObserver);

			mSyncStatusObserver.onStatusChanged(0);

			final int mask = ContentResolver.SYNC_OBSERVER_TYPE_PENDING | ContentResolver.SYNC_OBSERVER_TYPE_ACTIVE;
			mSyncObserverHandle = ContentResolver.addStatusChangeListener(mask, mSyncStatusObserver);

			Bundle bundle = new Bundle();
			// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(mAccount, "put.sailhero", bundle);

			onAlertsChanged();
		}

		@Override
		public void onPause() {
			super.onPause();

			Log.e(TAG, "onPause");

			if (mSyncObserverHandle != null) {
				ContentResolver.removeStatusChangeListener(mSyncObserverHandle);
				mSyncObserverHandle = null;
			}

			getActivity().getContentResolver().unregisterContentObserver(mAlertsObserver);
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);

			mContext = getActivity();
		}

		@Override
		public void onDetach() {
			super.onDetach();
		}

		private SyncStatusObserver mSyncStatusObserver = new SyncStatusObserver() {
			@Override
			public void onStatusChanged(int which) {
				Log.d(TAG, "status changed(" + which + ")");

				//				Account account = AccountUtils.getActiveAccount(mContext);
				//
				//				boolean syncActive = ContentResolver.isSyncActive(account, "put.sailhero");
				//				boolean syncPending = ContentResolver.isSyncPending(account, "put.sailhero");
				//
				//				if (!(syncActive || syncPending)) {
				//					onAlertsChanged();
				//				}
			}
		};

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = new String[] {
					SailHeroContract.Alert.COLUMN_NAME_ID + " as _id",
					SailHeroContract.Alert.COLUMN_NAME_TYPE
			};

			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Alert.CONTENT_URI, projection, null, null,
					SailHeroContract.Alert.COLUMN_NAME_TYPE);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (getActivity() == null || !isAdded()) {
				return;
			}

			Toast.makeText(mContext, "cursor load finished", Toast.LENGTH_SHORT).show();
			mAdapter.swapCursor(data);
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}

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

	@Override
	public void onLocationChanged(Location location) {
		Alert closestAlert = null;

		float minDistance = Float.MAX_VALUE;

		if (location.hasSpeed()) {
			Toast.makeText(AlertActivity.this, "Speed: " + location.getSpeed(), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(AlertActivity.this, "No speed attached.", Toast.LENGTH_SHORT).show();
		}

		/*
				AbstractList<Alert> alerts = mSettings.getAlerts();
				if (alerts != null) {
					for (final Alert alert : alerts) {
						float distance = location.distanceTo(alert.getLocation());
						if (distance < minDistance) {
							closestAlert = alert;
							minDistance = distance;
						}
					}
				}

				String msg = "Updated Location: " + Double.toString(location.getLatitude()) + ","
						+ Double.toString(location.getLongitude());
				// Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
				if (closestAlert != null) {
					Toast.makeText(this, "Closest alert id: " + closestAlert.getId(), Toast.LENGTH_SHORT).show();
					Log.i(TAG, "Closest alert id: " + closestAlert.getId());
				}
		*/

	}

}
