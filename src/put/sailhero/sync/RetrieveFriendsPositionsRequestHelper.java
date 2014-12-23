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
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrieveFriendsPositionsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_MAP = "map";
	private final static String PATH_FRIENDS = "friends";

	private LinkedList<FriendPosition> mRetrievedFriendsPositions;

	public RetrieveFriendsPositionsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendEncodedPath(PATH_MAP).appendEncodedPath(PATH_FRIENDS).build();

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

				LinkedList<FriendPosition> friendsPositions = new LinkedList<FriendPosition>();

				JSONArray friendsPositionsArray = (JSONArray) obj.get("friends");
				for (int i = 0; i < friendsPositionsArray.size(); i++) {
					JSONObject friendPositionObject = (JSONObject) friendsPositionsArray.get(i);
					FriendPosition friendPosition = new FriendPosition(friendPositionObject);

					friendsPositions.addLast(friendPosition);
				}

				mRetrievedFriendsPositions = friendsPositions;

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

		// Build hash table of incoming ports
		HashMap<Integer, FriendPosition> friendsPositionsMap = new HashMap<Integer, FriendPosition>();
		for (FriendPosition friendPosition : mRetrievedFriendsPositions) {
			Log.i(TAG, "hashmapping (" + friendPosition.getId() + ")");
			friendsPositionsMap.put(friendPosition.getId(), friendPosition);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		String[] projection = new String[] {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LATITUDE,
				SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LONGITUDE
		};

		int friendshipId, friendId;

		Cursor c = contentResolver.query(SailHeroContract.Friendship.CONTENT_URI, projection, null, null, null);

		while (c.moveToNext()) {
			friendshipId = c.getInt(0);
			friendId = c.getInt(1);

			FriendPosition friendPosition = friendsPositionsMap.get(friendId);

			Uri friendshipIdUri = SailHeroContract.Friendship.CONTENT_URI.buildUpon()
					.appendPath(Integer.toString(friendshipId))
					.build();

			if (friendPosition == null) {
				friendPosition = new FriendPosition(friendId, null, null);
			}

			Log.i(TAG, "scheduling update " + friendPosition.getLatitude() + ", " + friendPosition.getLongitude());

			batch.add(ContentProviderOperation.newUpdate(friendshipIdUri)
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LATITUDE, friendPosition.getLatitude())
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_LONGITUDE, friendPosition.getLongitude())
					.build());

		}
		c.close();

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}

	public static class FriendPosition {
		private Integer mId;
		private Double mLatitude;
		private Double mLongitude;

		public FriendPosition() {
		}

		public FriendPosition(Integer id, Double latitude, Double longitude) {
			mId = id;
			mLatitude = latitude;
			mLongitude = longitude;
		}

		public FriendPosition(JSONObject friendPositionObject) {
			this();

			setId(Integer.valueOf(friendPositionObject.get("id").toString()));

			JSONObject positionObject = (JSONObject) friendPositionObject.get("last_position");

			// TODO: json bug

			// Number latitudeObject = (Number) positionObject.get("latitude");
			// Number longitudeObject = (Number) positionObject.get("longitude");

			Number latitudeObject = null;
			Number longitudeObject = null;

			if (positionObject.get("latitude") != null) {
				latitudeObject = Double.valueOf((String) positionObject.get("latitude"));
			}

			if (positionObject.get("longitude") != null) {
				longitudeObject = Double.valueOf((String) positionObject.get("longitude"));
			}

			Log.w(TAG, latitudeObject + " " + longitudeObject);

			if (latitudeObject != null && longitudeObject != null) {
				Log.w(TAG, latitudeObject.doubleValue() + " " + longitudeObject.doubleValue());
				setLatitude(latitudeObject.doubleValue());
				setLongitude(longitudeObject.doubleValue());
			}
		}

		public void setId(Integer id) {
			mId = id;
		}

		public Integer getId() {
			return mId;
		}

		public void setLatitude(Double latitude) {
			mLatitude = latitude;
		}

		public Double getLatitude() {
			return mLatitude;
		}

		public void setLongitude(Double longitude) {
			mLongitude = longitude;
		}

		public Double getLongitude() {
			return mLongitude;
		}
	}
}
