package put.sailhero.android.app;

import put.sailhero.android.R;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;

public class TestMapActivity extends Activity {

	MapFragment mMapFragment;
	GoogleMap mGoogleMap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_map);
		if (savedInstanceState == null) {

			// getFragmentManager().beginTransaction()
			// .add(R.id.ActivityTestMapContainer, new
			// AlertsMapFragment()).commit();

			GoogleMapOptions googleMapOptions = new GoogleMapOptions();
			googleMapOptions.compassEnabled(true);
			
			mMapFragment = MapFragment.newInstance(googleMapOptions);
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.ActivityTestMapContainer, mMapFragment);
			fragmentTransaction.commit();

			

		}
	}

	@Override
	protected void onResume() {
		// mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
		mGoogleMap = mMapFragment.getMap();
		mGoogleMap.setMyLocationEnabled(true);
		
		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public static class AlertsMapFragment extends MapFragment {

		public AlertsMapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_test_map, container, false);
			return rootView;
		}
	}
}
