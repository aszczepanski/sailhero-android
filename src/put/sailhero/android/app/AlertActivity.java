package put.sailhero.android.app;

import java.util.AbstractList;
import java.util.LinkedList;

import put.sailhero.android.R;
import put.sailhero.android.util.CreateAlertRequest;
import put.sailhero.android.util.CreateAlertResponse;
import put.sailhero.android.util.CreateAlertResponseCreator;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.model.Alert;
import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

public class AlertActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener, LocationListener {

	private static final String TAG = "sailhero";

	private static final int MILLISECONDS_PER_SECOND = 1000;
	public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
	private static final long UPDATE_INTERVAL = MILLISECONDS_PER_SECOND
			* UPDATE_INTERVAL_IN_SECONDS;
	private static final int FASTEST_INTERVAL_IN_SECONDS = 1;

	private static final long FASTEST_INTERVAL = MILLISECONDS_PER_SECOND
			* FASTEST_INTERVAL_IN_SECONDS;

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private static LocationClient mLocationClient;
	private static LocationRequest mLocationRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new AlertFragment())
					.commit();
		}

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		mLocationClient = new LocationClient(this, this, this);
		mLocationRequest = LocationRequest.create();
		mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
		mLocationRequest.setInterval(UPDATE_INTERVAL);
		mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
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

	public static class AlertFragment extends Fragment {

		final static String TAG = "sailhero";

		private SailHeroService mService;
		private SailHeroSettings mSettings;

		private Spinner mSpinner;
		private Button mSubmitAlertButton;

		public AlertFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			mService = SailHeroService.getInstance();
			mSettings = mService.getSettings();

			View rootView = inflater.inflate(R.layout.fragment_alert, container, false);

			mSpinner = (Spinner) rootView.findViewById(R.id.FragmentAlertSpinner);
			ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
					R.array.alert_names, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			mSpinner.setAdapter(adapter);

			mSubmitAlertButton = (Button) rootView
					.findViewById(R.id.FragmentAlertSubmitAlertButton);
			mSubmitAlertButton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String selectedAlert = mSpinner.getSelectedItem().toString();

					final Location currentLocation = mLocationClient.getLastLocation();

					CreateAlertRequest request = new CreateAlertRequest(
							getAlertTypeFromString(selectedAlert), currentLocation, "");
					RequestAsyncTask task = new RequestAsyncTask(request,
							new CreateAlertResponseCreator(), getActivity(),
							new RequestAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(ProcessedResponse processedResponse) {
									CreateAlertResponse createAlertResponse = (CreateAlertResponse) processedResponse;

									AbstractList<Alert> alerts = mSettings.getAlerts();
									if (alerts == null) {
										alerts = new LinkedList<Alert>();
									}

									alerts.add(createAlertResponse.getAlert());

									mSettings.setAlerts(alerts);
									mSettings.save();
									Log.d(TAG, "created alert with id "
											+ createAlertResponse.getAlert().getId());
									Log.d(TAG, "alerts count = " + mSettings.getAlerts().size());
								}
							});
					task.execute();

					Toast.makeText(getActivity(), "Selected alert: " + selectedAlert,
							Toast.LENGTH_SHORT).show();
				}
			});

			return rootView;
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
			Toast.makeText(this, "Closest alert id: " + closestAlert.getId(), Toast.LENGTH_SHORT)
					.show();
			Log.i(TAG, "Closest alert id: " + closestAlert.getId());
		}

	}

}
