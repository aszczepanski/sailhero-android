package put.sailhero.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import put.sailhero.R;
import put.sailhero.sync.RetrieveFriendsPositionsRequestHelper;
import put.sailhero.util.SyncUtils;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class MapActivity extends BaseActivity {

	public final String TAG = "sailhero";

	private MapFragment mMapFragment;

	private ScheduledExecutorService mScheduledExecutorService;

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

		mScheduledExecutorService = Executors.newScheduledThreadPool(1);
		mScheduledExecutorService.scheduleAtFixedRate(new Runnable() {
			@Override
			public void run() {
				Log.i(TAG, "timeout");

				RetrieveFriendsPositionsRequestHelper requestHelper = new RetrieveFriendsPositionsRequestHelper(
						MapActivity.this);

				try {
					SyncUtils.doAuthenticatedRequest(MapActivity.this, requestHelper);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}, 0, 10, TimeUnit.SECONDS);
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
	protected void onDestroy() {
		super.onDestroy();

		mScheduledExecutorService.shutdown();
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MAP;
	}
}
