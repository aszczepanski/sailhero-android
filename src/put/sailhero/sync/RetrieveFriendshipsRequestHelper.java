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
import put.sailhero.model.Friendship;
import put.sailhero.provider.SailHeroContract;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrieveFriendshipsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_FRIENDSHIPS = "friendships";

	private LinkedList<Friendship> mRetrievedFriendships;

	public RetrieveFriendshipsRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_FRIENDSHIPS).build();

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
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				Log.e(TAG, responseBody);

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				mRetrievedFriendships = new LinkedList<Friendship>();

				//
				JSONArray pendingFriendshipsArray = (JSONArray) obj.get("pending");
				for (int i = 0; i < pendingFriendshipsArray.size(); i++) {
					JSONObject friendshipObject = (JSONObject) pendingFriendshipsArray.get(i);
					Friendship friendship = new Friendship(friendshipObject);
					friendship.setStatus(SailHeroContract.Friendship.STATUS_PENDING);

					mRetrievedFriendships.addLast(friendship);

					Log.i(TAG, friendshipObject.toString());
				}

				//
				JSONArray sentFriendshipsArray = (JSONArray) obj.get("sent");
				for (int i = 0; i < sentFriendshipsArray.size(); i++) {
					JSONObject friendshipObject = (JSONObject) sentFriendshipsArray.get(i);
					Friendship friendship = new Friendship(friendshipObject);
					friendship.setStatus(SailHeroContract.Friendship.STATUS_SENT);

					mRetrievedFriendships.addLast(friendship);

					Log.i(TAG, friendshipObject.toString());
				}

				//
				JSONArray acceptedFriendshipsArray = (JSONArray) obj.get("accepted");
				for (int i = 0; i < acceptedFriendshipsArray.size(); i++) {
					JSONObject friendshipObject = (JSONObject) acceptedFriendshipsArray.get(i);
					Friendship friendship = new Friendship(friendshipObject);
					friendship.setStatus(SailHeroContract.Friendship.STATUS_ACCEPTED);

					mRetrievedFriendships.addLast(friendship);

					Log.i(TAG, friendshipObject.toString());
				}

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

	// TODO !!!!!!!
	@Override
	public void storeData() throws SystemException {
		ContentResolver contentResolver = mContext.getContentResolver();

		// Build hash table of incoming alerts
		HashMap<Integer, Friendship> friendshipsMap = new HashMap<Integer, Friendship>();
		for (Friendship friendship : mRetrievedFriendships) {
			Log.i(TAG, friendship.getId() + " " + friendship.getStatus());
			friendshipsMap.put(friendship.getId(), friendship);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		Cursor c = contentResolver.query(SailHeroContract.Friendship.CONTENT_URI, Friendship.Query.PROJECTION, null,
				null, null);

		while (c.moveToNext()) {
			Friendship dbFriendship = new Friendship(c);
			Log.i(TAG, "friendship: " + dbFriendship.getId() + " " + dbFriendship.getStatus());

			Friendship retrievedFriendship = friendshipsMap.get(dbFriendship.getId());
			if (retrievedFriendship != null) {
				if (dbFriendship.equals(retrievedFriendship)) {
					// do nothing
				} else {
					Uri updateUri = SailHeroContract.Friendship.CONTENT_URI.buildUpon()
							.appendPath(dbFriendship.getId().toString())
							.build();
					batch.add(ContentProviderOperation.newUpdate(updateUri)
							.withValues(retrievedFriendship.toContentValues())
							.build());
				}
				friendshipsMap.remove(dbFriendship.getId());
			} else {
				// friendship doesn't exist anymore
				Uri deleteUri = SailHeroContract.Friendship.CONTENT_URI.buildUpon()
						.appendPath(dbFriendship.getId().toString())
						.build();
				Log.i(TAG, "Scheduling delete: " + deleteUri);
				batch.add(ContentProviderOperation.newDelete(deleteUri).build());
			}
		}
		c.close();

		for (Friendship friendship : friendshipsMap.values()) {
			Log.i(TAG, "Scheduling insert: friendship_id=" + friendship.getId());

			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Friendship.CONTENT_URI)
					.withValues(friendship.toContentValues())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
