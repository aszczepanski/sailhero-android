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

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Port;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrievePortsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String MAP_PATH = "map";
	private final static String GET_PORTS_REQUEST_PATH = "ports";

	private LinkedList<Port> mRetrievedPorts;

	public RetrievePortsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		final String apiHost = Config.API_HOST;
		final String apiPath = Config.API_PATH;
		final String version = Config.VERSION;
		final String i18n = Config.I18N;

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(MAP_PATH)
				.appendEncodedPath(GET_PORTS_REQUEST_PATH)
				.build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(EntityUtils.toString(mHttpResponse.getEntity()));

				LinkedList<Port> ports = new LinkedList<Port>();

				JSONArray portsArray = (JSONArray) obj.get("ports");
				for (int i = 0; i < portsArray.size(); i++) {
					JSONObject portObject = (JSONObject) portsArray.get(i);
					Port port = new Port(portObject);

					ports.addLast(port);
				}

				mRetrievedPorts = ports;

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			} catch (org.apache.http.ParseException e) {
				throw new SystemException(e.getMessage());
			} catch (IOException e) {
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

		// Build hash table of incoming ports
		HashMap<Integer, Port> portMap = new HashMap<Integer, Port>();
		for (Port port : mRetrievedPorts) {
			Log.i(TAG, port.getId() + " " + port.getName());
			portMap.put(port.getId(), port);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		Log.i(TAG, SailHeroContract.Port.CONTENT_URI.toString());

		String[] projection = new String[] {
				SailHeroContract.Port.COLUMN_NAME_ID,
				SailHeroContract.Port.COLUMN_NAME_NAME
		};

		int id;

		Cursor c = contentResolver.query(SailHeroContract.Port.CONTENT_URI, projection, null, null, null);

		while (c.moveToNext()) {
			Log.i(TAG, c.getInt(0) + " " + c.getString(1));
			id = c.getInt(0);

			Port port = portMap.get(id);
			if (port != null) {
				// alert already in database
				portMap.remove(id);
				// TODO: check if are equal
			} else {
				// alert should be deleted from database
				Uri deleteUri = SailHeroContract.Port.CONTENT_URI.buildUpon().appendPath(Integer.toString(id)).build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Port port : portMap.values()) {
			Log.i(TAG, "Scheduling insert: port_id=" + port.getId());
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Port.CONTENT_URI)
					.withValue(SailHeroContract.Port.COLUMN_NAME_ID, port.getId())
					.withValue(SailHeroContract.Port.COLUMN_NAME_NAME, port.getName())
					.withValue(SailHeroContract.Port.COLUMN_NAME_LATITUDE, port.getLocation().getLatitude())
					.withValue(SailHeroContract.Port.COLUMN_NAME_LONGITUDE, port.getLocation().getLongitude())
					.withValue(SailHeroContract.Port.COLUMN_NAME_WEBSITE, port.getWebsite())
					.withValue(SailHeroContract.Port.COLUMN_NAME_CITY, port.getName())
					.withValue(SailHeroContract.Port.COLUMN_NAME_STREET, port.getStreet())
					.withValue(SailHeroContract.Port.COLUMN_NAME_TELEPHONE, port.getTelephone())
					.withValue(SailHeroContract.Port.COLUMN_NAME_ADDITIONAL_INFO, port.getAdditionalInfo())
					.withValue(SailHeroContract.Port.COLUMN_NAME_SPOTS, port.getSpots())
					.withValue(SailHeroContract.Port.COLUMN_NAME_DEPTH, port.getDepth())
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION,
							port.isHasPowerConnection() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_WC, port.isHasWC() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_SHOWER, port.isHasShower() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_WASHBASIN, port.isHasWashbasin() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_DISHES, port.isHasDishes() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_WIFI, port.isHasWifi() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_PARKING, port.isHasParking() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_SLIP, port.isHasParking() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_WASHING_MACHINE,
							port.isHasWashingMachine() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION, port.isHasFuelStation() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET,
							port.isHasEmptyingChemicalToilet() ? 1 : 0)
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_PER_PERSON, port.getPricePerPerson())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_POWER_CONNECTION, port.getPricePowerConnection())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_WC, port.getPriceWC())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_SHOWER, port.getPriceShower())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_WASHBASIN, port.getPriceWashbasin())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_DISHES, port.getPriceDishes())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_WIFI, port.getPriceWifi())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_PARKING, port.getPriceParking())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_WASHING_MACHINE, port.getPriceWashingMachine())
					.withValue(SailHeroContract.Port.COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET,
							port.getPriceEmptyingChemicalToilet())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
