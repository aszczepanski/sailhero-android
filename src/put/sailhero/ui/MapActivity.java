package put.sailhero.ui;

import put.sailhero.R;
import android.app.FragmentManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseActivity {

	public final String TAG = "sailhero";

	private MapFragment mMapFragment;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);

		if (savedInstanceState == null) {
			GoogleMapOptions googleMapOptions = new GoogleMapOptions();
			googleMapOptions.compassEnabled(true);

			googleMapOptions.camera(new CameraPosition(new LatLng(54.043302, 21.738819), 10, 0, 0));

			FragmentManager fm = getFragmentManager();
			mMapFragment = (MapFragment) fm.findFragmentByTag("map");
		}

		overridePendingTransition(0, 0);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		if (mMapFragment == null) {
			mMapFragment = MapFragment.newInstance();
			getFragmentManager().beginTransaction().add(R.id.main_content, mMapFragment, "map").commit();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MAP;
	}
}
