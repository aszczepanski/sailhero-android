package put.sailhero.ui;

import put.sailhero.Config;
import put.sailhero.android.R;
import put.sailhero.model.Alert;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.ThrottledContentObserver;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements
		GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLoadedCallback,
		LoaderCallbacks<Cursor> {

	private GoogleMap mMap;

	public static MapFragment newInstance() {
		return new MapFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View mapView = super.onCreateView(inflater, container, savedInstanceState);

		View v = inflater.inflate(R.layout.fragment_map, container, false);
		FrameLayout layout = (FrameLayout) v.findViewById(R.id.map_container);

		layout.addView(mapView, 0);

		clearMap();

		if (mMap == null) {
			setupMap();
		}

		LoaderManager lm = getLoaderManager();
		lm.initLoader(AlertQuery._TOKEN, null, this);
		lm.initLoader(PortQuery._TOKEN, null, this);

		return v;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		activity.getContentResolver().registerContentObserver(SailHeroContract.Alert.CONTENT_URI, true, mObserver);
		activity.getContentResolver().registerContentObserver(SailHeroContract.Port.CONTENT_URI, true, mObserver);
	}

	@Override
	public void onDetach() {
		super.onDetach();

		getActivity().getContentResolver().unregisterContentObserver(mObserver);
	}

	private void clearMap() {
		if (mMap != null) {
			mMap.clear();
		}

		// TODO: clear all map elements
	}

	private void setupMap() {
		mMap = getMap();

		mMap.setOnMarkerClickListener(this);
		mMap.setOnInfoWindowClickListener(this);
		mMap.setOnMapLoadedCallback(this);
		mMap.setMyLocationEnabled(true);

		Log.d(Config.TAG, "Map setup complete.");
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean onMarkerClick(Marker arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker arg0) {
		// TODO Auto-generated method stub

	}

	private final ThrottledContentObserver mObserver = new ThrottledContentObserver(
			new ThrottledContentObserver.Callbacks() {
				@Override
				public void onThrottledContentObserverFired() {
					if (!isAdded()) {
						return;
					}

					clearMap();

					LoaderManager lm = getActivity().getLoaderManager();
					lm.restartLoader(AlertQuery._TOKEN, null, MapFragment.this);
					lm.restartLoader(PortQuery._TOKEN, null, MapFragment.this);
				}
			});

	private interface AlertQuery {
		int _TOKEN = 0x1;

		String[] PROJECTION = {
				SailHeroContract.Alert.COLUMN_NAME_ID,
				SailHeroContract.Alert.COLUMN_NAME_TYPE,
				SailHeroContract.Alert.COLUMN_NAME_LATITUDE,
				SailHeroContract.Alert.COLUMN_NAME_LONGITUDE,
				SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO
		};

		int ALERT_ID = 0;
		int ALERT_TYPE = 1;
		int ALERT_LATITUDE = 2;
		int ALERT_LONGITUDE = 3;
		int ALERT_ADDITIONAL_INFO = 4;
	}

	private interface PortQuery {
		int _TOKEN = 0x3;

		String[] PROJECTION = {
				SailHeroContract.Port.COLUMN_NAME_ID,
				SailHeroContract.Port.COLUMN_NAME_NAME,
				SailHeroContract.Port.COLUMN_NAME_LATITUDE,
				SailHeroContract.Port.COLUMN_NAME_LONGITUDE
		};

		int PORT_ID = 0;
		int PORT_NAME = 1;
		int PORT_LATITUDE = 2;
		int PORT_LONGITUDE = 3;
	}

	private void onAlertLoaderComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			while (cursor.moveToNext()) {
				Alert alert = new Alert();
				alert.setId(cursor.getInt(AlertQuery.ALERT_ID));
				alert.setAlertType(cursor.getString(AlertQuery.ALERT_TYPE));
				Location alertLocation = new Location("sailhero");
				alertLocation.setLatitude(cursor.getDouble(AlertQuery.ALERT_LATITUDE));
				alertLocation.setLongitude(cursor.getDouble(AlertQuery.ALERT_LONGITUDE));
				alert.setLocation(alertLocation);
				alert.setAdditionalInfo(cursor.getString(AlertQuery.ALERT_ADDITIONAL_INFO));

				LatLng pos = new LatLng(alert.getLocation().getLatitude(), alert.getLocation().getLongitude());
				Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(alert.getAlertType()));
			}
		}
	}

	private void onPortLoaderComplete(Cursor cursor) {
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			while (cursor.moveToNext()) {
				Port port = new Port();
				port.setId(cursor.getInt(PortQuery.PORT_ID));
				port.setName(cursor.getString(PortQuery.PORT_NAME));
				Location portLocation = new Location("sailhero");
				portLocation.setLatitude(cursor.getDouble(PortQuery.PORT_LATITUDE));
				portLocation.setLongitude(cursor.getDouble(PortQuery.PORT_LONGITUDE));
				port.setLocation(portLocation);

				LatLng pos = new LatLng(port.getLocation().getLatitude(), port.getLocation().getLongitude());
				Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(port.getName()));
			}
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		switch (id) {
		case AlertQuery._TOKEN: {
			Uri uri = SailHeroContract.Alert.CONTENT_URI;
			return new CursorLoader(getActivity(), uri, AlertQuery.PROJECTION, null, null, null);
		}
		case PortQuery._TOKEN: {
			Uri uri = SailHeroContract.Port.CONTENT_URI;
			return new CursorLoader(getActivity(), uri, PortQuery.PROJECTION, null, null, null);
		}
		default:
			return null;
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		if (getActivity() == null) {
			return;
		}
		switch (loader.getId()) {
		case AlertQuery._TOKEN:
			onAlertLoaderComplete(data);
			break;
		case PortQuery._TOKEN:
			onPortLoaderComplete(data);
			break;
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
