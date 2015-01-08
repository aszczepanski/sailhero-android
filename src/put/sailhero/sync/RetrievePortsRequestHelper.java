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

import put.sailhero.exception.InvalidRegionException;
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

	private final static String PATH_MAP = "map";
	private final static String PATH_PORTS = "ports";

	private LinkedList<Port> mRetrievedPorts;

	public RetrievePortsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendEncodedPath(PATH_MAP).appendEncodedPath(PATH_PORTS).build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException, InvalidRegionException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

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
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
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

		Cursor c = contentResolver.query(SailHeroContract.Port.CONTENT_URI, Port.Query.PROJECTION, null, null, null);

		while (c.moveToNext()) {
			Port dbPort = new Port(c);
			Log.i(TAG, dbPort.getId() + " " + dbPort.getName());

			Port retrievedPort = portMap.get(dbPort.getId());
			if (retrievedPort != null) {
				// alert already in database
				if (dbPort.equals(retrievedPort)) {
					// do nothing
				} else {
					Uri updateUri = SailHeroContract.Port.CONTENT_URI.buildUpon()
							.appendPath(retrievedPort.getId().toString())
							.build();
					Log.i(TAG, "Scheduling update: " + updateUri);
					batch.add(ContentProviderOperation.newUpdate(updateUri)
							.withValues(retrievedPort.toContentValues())
							.build());

				}
				portMap.remove(dbPort.getId());
			} else {
				// alert should be deleted from database
				Uri deleteUri = SailHeroContract.Port.CONTENT_URI.buildUpon()
						.appendPath(dbPort.getId().toString())
						.build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Port port : portMap.values()) {
			Log.i(TAG, "Scheduling insert: port_id=" + port.getId());
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Port.CONTENT_URI)
					.withValues(port.toContentValues())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
