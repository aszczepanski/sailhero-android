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
import put.sailhero.model.Region;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrieveRegionsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_REGIONS = "regions";

	private LinkedList<Region> mRegions;

	public RetrieveRegionsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_REGIONS).build();

		Log.e(TAG, uri.toString());

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

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				LinkedList<Region> regions = new LinkedList<Region>();

				JSONArray regionsArray = (JSONArray) obj.get("regions");
				for (int i = 0; i < regionsArray.size(); i++) {
					JSONObject regionObject = (JSONObject) regionsArray.get(i);
					Region region = new Region(regionObject);

					regions.addLast(region);
				}

				mRegions = regions;

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
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() throws SystemException {
		ContentResolver contentResolver = mContext.getContentResolver();

		// Build hash table of incoming regions
		HashMap<Integer, Region> regionMap = new HashMap<Integer, Region>();
		for (Region region : mRegions) {
			Log.i(TAG, region.getId() + " " + region.getFullName());
			regionMap.put(region.getId(), region);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		Cursor c = contentResolver.query(SailHeroContract.Region.CONTENT_URI, Region.Query.PROJECTION, null, null, null);

		while (c.moveToNext()) {
			Region dbRegion = new Region(c);
			Log.i(TAG, c.getInt(0) + " " + c.getString(2));

			Region retrievedRegion = regionMap.get(dbRegion.getId());
			if (retrievedRegion != null) {
				// alert already in database
				if (retrievedRegion.equals(dbRegion)) {
					// do nothing
				} else {
					Uri updateUri = SailHeroContract.Region.CONTENT_URI.buildUpon()
							.appendPath(dbRegion.getId().toString())
							.build();
					Log.i(TAG, "Scheduling update: " + updateUri);
					batch.add(ContentProviderOperation.newUpdate(updateUri)
							.withValues(retrievedRegion.toContentValues())
							.build());
				}
				regionMap.remove(dbRegion.getId());
			} else {
				// alert should be deleted from database
				Uri deleteUri = SailHeroContract.Region.CONTENT_URI.buildUpon()
						.appendPath(Integer.toString(dbRegion.getId()))
						.build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Region region : regionMap.values()) {
			Log.i(TAG, "Scheduling insert: region_id=" + region.getId());
			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Region.CONTENT_URI)
					.withValues(region.toContentValues())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
