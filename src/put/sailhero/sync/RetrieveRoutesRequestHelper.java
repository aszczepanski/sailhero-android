package put.sailhero.sync;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Route;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrieveRoutesRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_MAP = "map";
	private final static String PATH_ROUTES = "routes";

	private LinkedList<Route> mRetrievedRoutes;

	public RetrieveRoutesRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendEncodedPath(PATH_MAP).appendEncodedPath(PATH_ROUTES).build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.e(TAG, responseBody);

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				LinkedList<Route> routes = new LinkedList<Route>();

				JSONArray routesArray = (JSONArray) obj.get("routes");
				for (int i = 0; i < routesArray.size(); i++) {
					JSONObject routeObject = (JSONObject) routesArray.get(i);
					Route route = new Route(routeObject);

					routes.addLast(route);
				}

				mRetrievedRoutes = routes;

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			} catch (org.apache.http.ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}

	@Override
	public void storeData() throws SystemException {
		ContentResolver contentResolver = mContext.getContentResolver();

		//		contentResolver.delete(SailHeroContract.Route.Pin.CONTENT_URI, null, null);
		//		contentResolver.delete(SailHeroContract.Route.CONTENT_URI, null, null);
		//
		//		for (Route route : mRetrievedRoutes) {
		//			Log.i(TAG, route.getName());
		//			ContentValues values = new ContentValues();
		//			values.put(SailHeroContract.Route.COLUMN_NAME_ID, route.getId());
		//			values.put(SailHeroContract.Route.COLUMN_NAME_NAME, route.getName());
		//			contentResolver.insert(SailHeroContract.Route.CONTENT_URI, values);
		//
		//			int positionInRoute = 0;
		//
		//			for (Route.Pin pin : route.getPins()) {
		//				values = new ContentValues();
		//				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LATITUDE, pin.getLatitude());
		//				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LONGITUDE, pin.getLongitude());
		//				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_ROUTE_ID, route.getId());
		//				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_POSITION_IN_ROUTE, positionInRoute);
		//				contentResolver.insert(SailHeroContract.Route.Pin.CONTENT_URI, values);
		//
		//				positionInRoute++;
		//			}
		//		}

		/////////////////////////////////////
		HashMap<Integer, Route> retrievedRoutesMap = new HashMap<Integer, Route>();
		for (Route route : mRetrievedRoutes) {
			retrievedRoutesMap.put(route.getId(), route);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

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

		Cursor c = contentResolver.query(SailHeroContract.Route.Pin.CONTENT_JOIN_ROUTES_URI, PROJECTION, null, null,
				null);

		HashMap<Integer, Route> dbRoutesMap = new HashMap<Integer, Route>();

		while (c.moveToNext()) {
			Route.Pin pin = new Route.Pin();
			pin.setLatitude(c.getDouble(ROUTE_PIN_LATITUDE));
			pin.setLongitude(c.getDouble(ROUTE_PIN_LONGITUDE));

			Integer routeId = c.getInt(ROUTE_ID);

			Route route = dbRoutesMap.get(routeId);
			if (route == null) {
				route = new Route();
				route.setId(routeId);
				route.setName(c.getString(ROUTE_NAME));
				LinkedList<Route.Pin> pins = new LinkedList<Route.Pin>();
				pins.addLast(pin);
				route.setPins(pins);

				dbRoutesMap.put(routeId, route);
			} else {
				LinkedList<Route.Pin> pins = route.getPins();
				pins.addLast(pin);
				route.setPins(pins);
			}
		}

		for (Route dbRoute : dbRoutesMap.values()) {
			Log.d(TAG, "dbRoute: " + dbRoute.getName());
			Route retrievedRoute = retrievedRoutesMap.get(dbRoute.getId());

			if (retrievedRoute != null) {
				// already in db
				Log.i(TAG, "route already in database");
				if (dbRoute.equals(retrievedRoute)) {
					// do nothing
				} else {
					Log.i(TAG, "Scheduling insert: " + retrievedRoute.getName());
					ContentValues values = new ContentValues();
					values.put(SailHeroContract.Route.COLUMN_NAME_ID, retrievedRoute.getId());
					values.put(SailHeroContract.Route.COLUMN_NAME_NAME, retrievedRoute.getName());
					batch.add(ContentProviderOperation.newDelete(SailHeroContract.Route.Pin.CONTENT_URI)
							.withValue(SailHeroContract.Route.Pin.COLUMN_NAME_ROUTE_ID, dbRoute.getId())
							.build());
					batch.add(ContentProviderOperation.newUpdate(
							SailHeroContract.Route.CONTENT_URI.buildUpon()
									.appendPath(dbRoute.getId().toString())
									.build())
							.withValues(values)
							.build());

					int positionInRoute = 0;

					for (Route.Pin pin : retrievedRoute.getPins()) {
						values = new ContentValues();
						values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LATITUDE, pin.getLatitude());
						values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LONGITUDE, pin.getLongitude());
						values.put(SailHeroContract.Route.Pin.COLUMN_NAME_ROUTE_ID, retrievedRoute.getId());
						values.put(SailHeroContract.Route.Pin.COLUMN_NAME_POSITION_IN_ROUTE, positionInRoute);
						batch.add(ContentProviderOperation.newInsert(SailHeroContract.Route.Pin.CONTENT_URI)
								.withValues(values)
								.build());

						positionInRoute++;
					}
				}
				retrievedRoutesMap.remove(retrievedRoute.getId());
			} else {
				Uri deleteUri = SailHeroContract.Route.CONTENT_URI.buildUpon()
						.appendPath(dbRoute.getId().toString())
						.build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(SailHeroContract.Route.Pin.CONTENT_URI)
						.withSelection(SailHeroContract.Route.Pin.COLUMN_NAME_ROUTE_ID + "=?", new String[] {
							dbRoute.getId().toString()
						})
						.build());
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}

		for (Route retrievedRoute : retrievedRoutesMap.values()) {
			Log.i(TAG, "Scheduling insert: " + retrievedRoute.getName());
			ContentValues values = new ContentValues();
			values.put(SailHeroContract.Route.COLUMN_NAME_ID, retrievedRoute.getId());
			values.put(SailHeroContract.Route.COLUMN_NAME_NAME, retrievedRoute.getName());
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Route.CONTENT_URI).withValues(values).build());

			int positionInRoute = 0;

			for (Route.Pin pin : retrievedRoute.getPins()) {
				values = new ContentValues();
				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LATITUDE, pin.getLatitude());
				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_LONGITUDE, pin.getLongitude());
				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_ROUTE_ID, retrievedRoute.getId());
				values.put(SailHeroContract.Route.Pin.COLUMN_NAME_POSITION_IN_ROUTE, positionInRoute);
				batch.add(ContentProviderOperation.newInsert(SailHeroContract.Route.Pin.CONTENT_URI)
						.withValues(values)
						.build());

				positionInRoute++;
			}
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
