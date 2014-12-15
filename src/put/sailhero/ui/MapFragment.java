package put.sailhero.ui;

import java.util.HashMap;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import android.app.Activity;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
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

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements
		GoogleMap.OnInfoWindowClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapLoadedCallback,
		LoaderCallbacks<Cursor> {

	private GoogleMap mMap;

	private HashMap<Marker, Integer> markerPortIdMap;
	private HashMap<Marker, Integer> markerAlertIdMap;

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
	}

	@Override
	public void onDetach() {
		super.onDetach();
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

		Location lastKnownLocation = PrefUtils.getLastKnownLocation(getActivity());
		if (lastKnownLocation != null) {
			final float CAMERA_ZOOM = 15.0f;

			CameraUpdate camera = CameraUpdateFactory.newCameraPosition(new CameraPosition.Builder().bearing(
					lastKnownLocation.getBearing())
					.target(new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()))
					.zoom(CAMERA_ZOOM)
					.tilt(0f)
					.build());

			// mMap.animateCamera(camera);
			mMap.moveCamera(camera);
		}

		Log.d(Config.TAG, "Map setup complete.");
	}

	@Override
	public void onMapLoaded() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Integer portId = markerPortIdMap.get(marker);

		if (portId != null) {
			Intent intent = new Intent(getActivity(), PortActivity.class);
			intent.putExtra("port_id", portId);
			startActivity(intent);
		}
	}

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
		if (markerAlertIdMap == null) {
			markerAlertIdMap = new HashMap<Marker, Integer>();
		} else {
			for (Marker marker : markerAlertIdMap.keySet()) {
				marker.remove();
			}
		}

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

				markerAlertIdMap.put(marker, alert.getId());
			}
		}
	}

	private void onPortLoaderComplete(Cursor cursor) {
		if (markerPortIdMap == null) {
			markerPortIdMap = new HashMap<Marker, Integer>();
		} else {
			for (Marker marker : markerPortIdMap.keySet()) {
				marker.remove();
			}
		}

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
				Marker marker = mMap.addMarker(new MarkerOptions().position(pos)
						.title(port.getName())
						.snippet("click to see the details")
						.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

				markerPortIdMap.put(marker, port.getId());
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
