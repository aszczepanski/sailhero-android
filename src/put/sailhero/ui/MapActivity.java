package put.sailhero.ui;

import java.util.HashMap;

import put.sailhero.R;
import android.app.FragmentManager;
import android.os.Bundle;

import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class MapActivity extends BaseActivity /*implements LoaderManager.LoaderCallbacks<Cursor>*/{

	public final String TAG = "sailhero";

	private MapFragment mMapFragment;

	private HashMap<Marker, Integer> markerPortIdMap;

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
	/*
		@Override
		protected void onResume() {
			super.onResume();

			mGoogleMap = mMapFragment.getMap();
			// mGoogleMap.clear();
			mGoogleMap.setMyLocationEnabled(true);

			mAccount = AccountUtils.getActiveAccount(mContext);

			mAlertsObserver = new ThrottledContentObserver(new ThrottledContentObserver.Callbacks() {
				@Override
				public void onThrottledContentObserverFired() {
					onPortsChanged();
				}
			});
			getContentResolver().registerContentObserver(SailHeroContract.Port.CONTENT_URI, true, mAlertsObserver);

			//		AbstractList<Port> ports = mSettings.getPorts();
			//		markerPortMap = new HashMap<Marker, Port>();
			//
			//		for (Port port : ports) {
			//			LatLng pos = new LatLng(port.getLocation().getLatitude(), port.getLocation().getLongitude());
			//			Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(pos).title(port.getName()));
			//			markerPortMap.put(marker, port);
			//		}

			mGoogleMap.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
				@Override
				public void onInfoWindowClick(Marker marker) {
					//				Port port = markerPortMap.get(marker);

					Intent intent = new Intent(MapActivity.this, PortActivity.class);
					intent.putExtra("port_id", markerPortIdMap.get(marker));
					startActivity(intent);
				}
			});

			Bundle bundle = new Bundle();
			// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(mAccount, SailHeroContract.CONTENT_AUTHORITY, bundle);

			onPortsChanged();
		}

		private void onPortsChanged() {
			Toast.makeText(mContext, "onPortsChanged()", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "onPortsChanged()");

			getLoaderManager().restartLoader(1, null, MapActivity.this);
		}

		@Override
		protected void onPause() {
			super.onPause();

			getContentResolver().unregisterContentObserver(mAlertsObserver);
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			String[] projection = new String[] {
					SailHeroContract.Port.COLUMN_NAME_ID,
					SailHeroContract.Port.COLUMN_NAME_NAME,
					SailHeroContract.Port.COLUMN_NAME_LATITUDE,
					SailHeroContract.Port.COLUMN_NAME_LONGITUDE,
			};

			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Port.CONTENT_URI, projection, null, null, null);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (markerPortIdMap == null) {
				markerPortIdMap = new HashMap<Marker, Integer>();
			}

			HashMap<Integer, Port> loadedPortMap = new HashMap<Integer, Port>();

			int id;
			String name;
			double latitude, longitude;

			while (data.moveToNext()) {
				id = data.getInt(0);
				name = data.getString(1);
				latitude = data.getDouble(2);
				longitude = data.getDouble(3);

				Port port = new Port();
				port.setId(id);
				port.setName(name);
				Location portLocation = new Location("sailhero");
				portLocation.setLatitude(latitude);
				portLocation.setLongitude(longitude);
				port.setLocation(portLocation);

				loadedPortMap.put(id, port);
			}

			Iterator it = markerPortIdMap.entrySet().iterator();
			while (it.hasNext()) {
				HashMap.Entry pairs = (HashMap.Entry) it.next();
				Integer portId = (Integer) pairs.getValue();
				if (loadedPortMap.containsKey(portId)) {
					loadedPortMap.remove(portId);
				} else {
					Marker portMarker = (Marker) pairs.getKey();
					portMarker.remove();
					it.remove();
				}
			}

			for (HashMap.Entry<Integer, Port> entry : loadedPortMap.entrySet()) {
				Integer entryId = entry.getKey();
				Port port = entry.getValue();
				LatLng pos = new LatLng(port.getLocation().getLatitude(), port.getLocation().getLongitude());
				Marker marker = mGoogleMap.addMarker(new MarkerOptions().position(pos).title(port.getName()));
				markerPortIdMap.put(marker, port.getId());
			}
		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
		*/
}
