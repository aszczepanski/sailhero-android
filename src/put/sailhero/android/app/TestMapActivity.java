package put.sailhero.android.app;

import java.util.AbstractList;
import java.util.HashMap;

import put.sailhero.android.R;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.model.Port;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class TestMapActivity extends BaseActivity {

	public final String TAG = "sailhero";

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private MapFragment mMapFragment;
	private GoogleMap mGoogleMap;

	private HashMap<Marker, Port> markerPortMap;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_map);

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		if (savedInstanceState == null) {

			// getFragmentManager().beginTransaction()
			// .add(R.id.ActivityTestMapContainer, new
			// PortsMapFragment()).commit();

			GoogleMapOptions googleMapOptions = new GoogleMapOptions();
			googleMapOptions.compassEnabled(true);

			googleMapOptions.camera(new CameraPosition(new LatLng(54.043302, 21.738819), 10, 0, 0));
			
			mMapFragment = MapFragment.newInstance(googleMapOptions);
			FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
			fragmentTransaction.add(R.id.main_content, mMapFragment);
			fragmentTransaction.commit();

		}
		
		super.overridePendingTransition(0,0);
	}
	
	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MAP;
	}

	@Override
	protected void onResume() {
		mGoogleMap = mMapFragment.getMap();
		mGoogleMap.clear();
		mGoogleMap.setMyLocationEnabled(true);
		
		AbstractList<Port> ports = mSettings.getPorts();
		markerPortMap = new HashMap<Marker, Port>();
		
		for (Port port : ports) {
			LatLng pos = new LatLng(port.getLocation().getLatitude(), port.getLocation()
					.getLongitude());
			Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(pos).title(
					port.getName()));
			markerPortMap.put(marker, port);
		}
		
		mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(Marker marker) {
				Port port = markerPortMap.get(marker);
				Toast.makeText(TestMapActivity.this, "city: " + port.getCity(), Toast.LENGTH_SHORT).show();
				
				Intent intent = new Intent(TestMapActivity.this, PortActivity.class);
				intent.putExtra("port_id", port.getId());
				startActivity(intent);
			}
		});

		super.onResume();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	public static class PortsMapFragment extends MapFragment {

		public PortsMapFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_test_map, container, false);
			return rootView;
		}
	}
}
