package put.sailhero.ui;

import put.sailhero.android.R;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

public class AlertMapActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener {

	public final String TAG = "sailhero";

	private MapFragment mMapFragment;
	private GoogleMap mGoogleMap;

	private LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alert_map);

		mLocationClient = new LocationClient(this, this, this);

		if (savedInstanceState == null) {
			// getFragmentManager().beginTransaction().add(R.id.container, new
			// AlertsMapFragment())
			// .commit();

			GoogleMapOptions googleMapOptions = new GoogleMapOptions();
			googleMapOptions.compassEnabled(true);

			// googleMapOptions.camera(new CameraPosition(new LatLng(54.043302,
			// 21.738819), 10, 0, 0));

			mMapFragment = MapFragment.newInstance(googleMapOptions);
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.container, mMapFragment);
			fragmentTransaction.commit();
		}
	}

	@Override
	protected void onResume() {
		mGoogleMap = mMapFragment.getMap();
		mGoogleMap.clear();
		mGoogleMap.setMyLocationEnabled(true);

		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
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

	public static class AlertsMapFragment extends Fragment {

		public AlertsMapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_alert_map, container, false);
			return rootView;
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
