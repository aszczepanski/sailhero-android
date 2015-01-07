package put.sailhero.ui;

import java.util.HashMap;
import java.util.LinkedList;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Alert;
import put.sailhero.model.Friendship;
import put.sailhero.model.Port;
import put.sailhero.model.Route;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.StringUtils;
import android.app.Activity;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

public class MapFragment extends com.google.android.gms.maps.MapFragment implements GoogleMap.OnMarkerClickListener,
		LoaderCallbacks<Cursor> {

	private GoogleMap mMap;

	private HashMap<Marker, Integer> mMarkerPortIdMap;
	private HashMap<Marker, Alert> mMarkerAlertMap;
	private HashMap<Marker, Integer> mMarkerFriendshipIdMap;
	private HashMap<Polyline, Integer> mPolylineRouteIdMap;

	private Marker mLastClickedMarker;

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
		lm.initLoader(FriendshipQuery._TOKEN, null, this);
		lm.initLoader(RouteQuery._TOKEN, null, this);

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
	public boolean onMarkerClick(Marker marker) {
		if (mLastClickedMarker != null && mLastClickedMarker.equals(marker)) {
			mLastClickedMarker = null;

			Integer portId = mMarkerPortIdMap.get(marker);
			if (portId != null) {
				Intent intent = new Intent(getActivity(), PortActivity.class);
				intent.putExtra("port_id", portId);
				startActivity(intent);
				return true;
			}

			Alert alert = mMarkerAlertMap.get(marker);
			if (alert != null) {
				DialogFragment alertResponseDialogFragment = new AlertResponseDialogFragment(getActivity(), alert);
				alertResponseDialogFragment.show(getFragmentManager(), "response");
			}

			return true;
		} else {
			mLastClickedMarker = marker;
			return false;
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

	private interface FriendshipQuery {
		int _TOKEN = 0x5;

		String[] PROJECTION = {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LATITUDE,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LONGITUDE,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_AVATAR_URL
		};

		int FRIENDSHIP_ID = 0;
		int FRIEND_NAME = 1;
		int FRIEND_SURNAME = 2;
		int FRIEND_LATITUDE = 3;
		int FRIEND_LONGITUDE = 4;
		int FRIEND_AVATAR_URL = 5;
	}

	private interface RouteQuery {
		int _TOKEN = 0x7;

		String[] PROJECTION = {
				SailHeroContract.Route.COLUMN_NAME_ID,
				SailHeroContract.Route.COLUMN_NAME_NAME,
				SailHeroContract.Route.Pin.COLUMN_NAME_LATITUDE,
				SailHeroContract.Route.Pin.COLUMN_NAME_LONGITUDE
		};

		int ROUTE_ID = 0;
		int ROUTE_NAME = 1;
		int ROUTE_PIN_LATITUDE = 2;
		int ROUTE_PIN_LONGITUDE = 3;
	}

	private void onAlertLoaderComplete(Cursor cursor) {
		if (mMarkerAlertMap == null) {
			mMarkerAlertMap = new HashMap<Marker, Alert>();
		} else {
			for (Marker marker : mMarkerAlertMap.keySet()) {
				marker.remove();
			}
		}

		IconGenerator iconFactory = new IconGenerator(getActivity());
		iconFactory.setStyle(IconGenerator.STYLE_RED);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			while (cursor.moveToNext()) {
				Alert alert = new Alert();
				alert.setId(cursor.getInt(AlertQuery.ALERT_ID));
				alert.setAlertType(cursor.getString(AlertQuery.ALERT_TYPE));
				alert.setLatitude(cursor.getDouble(AlertQuery.ALERT_LATITUDE));
				alert.setLongitude(cursor.getDouble(AlertQuery.ALERT_LONGITUDE));
				alert.setAdditionalInfo(cursor.getString(AlertQuery.ALERT_ADDITIONAL_INFO));

				LatLng pos = new LatLng(alert.getLocation().getLatitude(), alert.getLocation().getLongitude());

				MarkerOptions markerOptions = new MarkerOptions().icon(
						BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(StringUtils.getStringForAlertType(
								getActivity(), alert.getAlertType()))))
						.position(pos)
						.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
				Marker marker = mMap.addMarker(markerOptions);

				mMarkerAlertMap.put(marker, alert);
			}
		}
	}

	private void onPortLoaderComplete(Cursor cursor) {
		if (mMarkerPortIdMap == null) {
			mMarkerPortIdMap = new HashMap<Marker, Integer>();
		} else {
			for (Marker marker : mMarkerPortIdMap.keySet()) {
				marker.remove();
			}
		}

		IconGenerator iconFactory = new IconGenerator(getActivity());
		iconFactory.setStyle(IconGenerator.STYLE_PURPLE);

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			while (cursor.moveToNext()) {
				Port port = new Port();
				port.setId(cursor.getInt(PortQuery.PORT_ID));
				port.setName(cursor.getString(PortQuery.PORT_NAME));
				port.setLatitude(cursor.getDouble(PortQuery.PORT_LATITUDE));
				port.setLongitude(cursor.getDouble(PortQuery.PORT_LONGITUDE));

				LatLng pos = new LatLng(port.getLocation().getLatitude(), port.getLocation().getLongitude());

				MarkerOptions markerOptions = new MarkerOptions().icon(
						BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(port.getName())))
						.position(pos)
						.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
				Marker marker = mMap.addMarker(markerOptions);

				mMarkerPortIdMap.put(marker, port.getId());
			}
		}
	}

	private void onFriendshipLoaderComplete(Cursor cursor) {
		// TODO: DRY
		if (mMarkerFriendshipIdMap == null) {
			mMarkerFriendshipIdMap = new HashMap<Marker, Integer>();
		} else {
			for (Marker marker : mMarkerFriendshipIdMap.keySet()) {
				marker.remove();
			}
		}

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			IconGenerator iconFactory = new IconGenerator(getActivity());
			iconFactory.setStyle(IconGenerator.STYLE_GREEN);

			while (cursor.moveToNext()) {
				Log.d(Config.TAG, "friendship element");

				Friendship friendship = new Friendship();
				friendship.setId(cursor.getInt(FriendshipQuery.FRIENDSHIP_ID));
				User friend = new User();
				friend.setName(cursor.getString(FriendshipQuery.FRIEND_NAME));
				friend.setSurname(cursor.getString(FriendshipQuery.FRIEND_SURNAME));
				friend.setAvatarUrl(cursor.getString(FriendshipQuery.FRIEND_AVATAR_URL));

				Double friendLatitude = cursor.getDouble(FriendshipQuery.FRIEND_LATITUDE);
				Double friendLongitude = cursor.getDouble(FriendshipQuery.FRIEND_LONGITUDE);

				friendship.setFriend(friend);

				if (friendLatitude != null && friendLongitude != null) {
					Log.d(Config.TAG, "friendship element: " + friendLatitude + ", " + friendLongitude);

					LatLng pos = new LatLng(friendLatitude, friendLongitude);

					MarkerOptions markerOptions = new MarkerOptions().icon(
							BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(friend.getName() + " "
									+ friend.getSurname())))
							.position(pos)
							.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());
					Marker marker = mMap.addMarker(markerOptions);

					mMarkerFriendshipIdMap.put(marker, friendship.getId());
				}

			}
		}
	}

	private void onRouteLoaderComplete(Cursor cursor) {
		// TODO: DRY
		if (mPolylineRouteIdMap == null) {
			mPolylineRouteIdMap = new HashMap<Polyline, Integer>();
		} else {
			for (Polyline polyline : mPolylineRouteIdMap.keySet()) {
				polyline.remove();
			}
		}

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToPosition(-1);

			HashMap<Integer, Route> routesMap = new HashMap<Integer, Route>();

			while (cursor.moveToNext()) {
				Route.Pin pin = new Route.Pin();
				pin.setLatitude(cursor.getDouble(RouteQuery.ROUTE_PIN_LATITUDE));
				pin.setLongitude(cursor.getDouble(RouteQuery.ROUTE_PIN_LONGITUDE));

				Integer routeId = cursor.getInt(RouteQuery.ROUTE_ID);

				Route route = routesMap.get(routeId);
				if (route == null) {
					route = new Route();
					route.setId(routeId);
					route.setName(cursor.getString(RouteQuery.ROUTE_NAME));
					LinkedList<Route.Pin> pins = new LinkedList<Route.Pin>();
					pins.addLast(pin);
					route.setPins(pins);

					routesMap.put(routeId, route);
				} else {
					LinkedList<Route.Pin> pins = route.getPins();
					pins.addLast(pin);
					route.setPins(pins);
				}
			}

			for (Route route : routesMap.values()) {
				PolylineOptions routePolylineOptions = new PolylineOptions();
				for (Route.Pin pin : route.getPins()) {
					Log.d(Config.TAG, "adding: " + pin.getLatitude() + ", " + pin.getLongitude());
					routePolylineOptions.add(new LatLng(pin.getLatitude(), pin.getLongitude()));
				}

				routePolylineOptions.color(Color.GREEN);
				Polyline polyline = mMap.addPolyline(routePolylineOptions);

				mPolylineRouteIdMap.put(polyline, route.getId());
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
		case FriendshipQuery._TOKEN: {
			Uri uri = SailHeroContract.Friendship.CONTENT_URI;
			return new CursorLoader(getActivity(), uri, FriendshipQuery.PROJECTION, null, null, null);
		}
		case RouteQuery._TOKEN: {
			Uri uri = SailHeroContract.Route.Pin.CONTENT_JOIN_ROUTES_URI;
			return new CursorLoader(getActivity(), uri, RouteQuery.PROJECTION, null, null, null);
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
		case FriendshipQuery._TOKEN:
			onFriendshipLoaderComplete(data);
			break;
		case RouteQuery._TOKEN:
			onRouteLoaderComplete(data);
			break;
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
	}

}
