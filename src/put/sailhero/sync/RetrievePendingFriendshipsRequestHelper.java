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
import put.sailhero.model.Friendship;
import put.sailhero.model.User;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.util.PrefUtils;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.util.Log;

public class RetrievePendingFriendshipsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String FRIENDSHIPS_REQUEST_PATH = "friendships/pending";

	private LinkedList<Friendship> mRetrievedFriendships;

	public RetrievePendingFriendshipsRequestHelper(Context context) {
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
				.appendEncodedPath(FRIENDSHIPS_REQUEST_PATH)
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

				LinkedList<Friendship> friendships = new LinkedList<Friendship>();

				JSONArray friendshipsArray = (JSONArray) obj.get("friendships");
				for (int i = 0; i < friendshipsArray.size(); i++) {
					JSONObject friendshipObject = (JSONObject) friendshipsArray.get(i);
					Friendship friendship = new Friendship(friendshipObject);

					friendships.addLast(friendship);

					Log.i(TAG, friendshipObject.toString());
				}

				mRetrievedFriendships = friendships;

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
		User currentUser = PrefUtils.getUser(mContext);

		ContentResolver contentResolver = mContext.getContentResolver();

		// Build hash table of incoming alerts
		HashMap<Integer, Friendship> friendshipMap = new HashMap<Integer, Friendship>();
		for (Friendship friendship : mRetrievedFriendships) {
			Log.i(TAG, friendship.getId() + " " + friendship.getStatus());
			friendshipMap.put(friendship.getId(), friendship);
		}

		ArrayList<ContentProviderOperation> batch = new ArrayList<ContentProviderOperation>();

		Log.i(TAG, SailHeroContract.Friendship.CONTENT_URI.toString());

		String[] projection = new String[] {
				SailHeroContract.Friendship.COLUMN_NAME_ID,
				SailHeroContract.Friendship.COLUMN_NAME_STATUS
		};

		Cursor c = contentResolver.query(SailHeroContract.Friendship.CONTENT_URI, projection, null, null, null);

		while (c.moveToNext()) {
			Log.i(TAG, "friendship: " + c.getInt(0) + " " + c.getInt(1));
			int id = c.getInt(0);
			int status = c.getInt(1);

			Friendship friendship = friendshipMap.get(id);
			if (friendship != null) {
				if ((friendship.getStatus() == Friendship.STATUS_NOT_ACCEPTED && (status == SailHeroContract.Friendship.STATUS_SENT || status == SailHeroContract.Friendship.STATUS_PENDING))
						|| (friendship.getStatus() == Friendship.STATUS_ACCEPTED && status == SailHeroContract.Friendship.STATUS_ACCEPTED)
						|| (friendship.getStatus() == Friendship.STATUS_BLOCKED && status == SailHeroContract.Friendship.STATUS_BLOCKED)) {
					// friendship already in database, no need to update
					// TODO: check friend data
					friendshipMap.remove(id);
				} else if (friendship.getStatus() == Friendship.STATUS_ACCEPTED
						&& (status == SailHeroContract.Friendship.STATUS_SENT || status == SailHeroContract.Friendship.STATUS_PENDING)) {
					Uri updateUri = SailHeroContract.Friendship.CONTENT_URI.buildUpon()
							.appendPath(Integer.toString(id))
							.build();
					Log.i(TAG, "Scheduling update: " + updateUri);
					batch.add(ContentProviderOperation.newUpdate(updateUri)
							.withValue(SailHeroContract.Friendship.COLUMN_NAME_STATUS,
									SailHeroContract.Friendship.STATUS_ACCEPTED)
							.build());
				} else if (friendship.getStatus() == Friendship.STATUS_BLOCKED) {
					Uri updateUri = SailHeroContract.Friendship.CONTENT_URI.buildUpon()
							.appendPath(Integer.toString(id))
							.build();
					Log.i(TAG, "Scheduling update: " + updateUri);
					batch.add(ContentProviderOperation.newUpdate(updateUri)
							.withValue(SailHeroContract.Friendship.COLUMN_NAME_STATUS,
									SailHeroContract.Friendship.STATUS_BLOCKED)
							.build());
				} else {
					// TODO: error - should not be here
				}
			} else {
				// friendship cannot be deleted from database - only state can be updated
			}
		}
		c.close();

		for (Friendship friendship : friendshipMap.values()) {
			Log.i(TAG, "Scheduling insert: friendship_id=" + friendship.getId());

			int status;
			if (friendship.getStatus() == Friendship.STATUS_ACCEPTED) {
				status = SailHeroContract.Friendship.STATUS_ACCEPTED;
			} else if (friendship.getUser().getId() == currentUser.getId()) {
				status = SailHeroContract.Friendship.STATUS_SENT;
			} else {
				status = SailHeroContract.Friendship.STATUS_PENDING;
			}

			User friendToAdd;
			if (friendship.getUser().getId() == currentUser.getId()) {
				friendToAdd = friendship.getFriend();
			} else {
				friendToAdd = friendship.getUser();
			}

			batch.add(ContentProviderOperation.newInsert(SailHeroContract.Friendship.CONTENT_URI)
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_ID, friendship.getId())
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_STATUS, status)
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID, friendToAdd.getId())
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL, friendToAdd.getEmail())
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME, friendToAdd.getName())
					.withValue(SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME, friendToAdd.getSurname())
					.build());
		}

		try {
			contentResolver.applyBatch(SailHeroContract.CONTENT_AUTHORITY, batch);
		} catch (RemoteException | OperationApplicationException e) {
			throw new SystemException(e.getMessage());
		}
	}
}
