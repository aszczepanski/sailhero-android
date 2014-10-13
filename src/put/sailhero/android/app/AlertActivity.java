package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.CreateAlertRequest;
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

public class AlertActivity extends Activity implements
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	private static LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.container, new AlertFragment())
					.commit();
		}

		mLocationClient = new LocationClient(this, this, this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	public static class AlertFragment extends Fragment {

		final static String TAG = "sailhero";

		private Spinner mSpinner;
		private Button mSubmitAlertButton;

		public AlertFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
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
					CreateAlertAsyncTask task = new CreateAlertAsyncTask(request, getActivity(),
							new CreateAlertAsyncTask.CreateAlertListener() {
								@Override
								public void onAlertCreated() {
									Log.i(TAG,
											"Alert created with location "
													+ String.valueOf(currentLocation.getLatitude())
													+ ", "
													+ String.valueOf(currentLocation.getLongitude()));

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

	}

	@Override
	public void onDisconnected() {

	}
}
