package put.sailhero.gcm;

import put.sailhero.util.SyncUtils;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

	public final static String TAG = "sailhero";

	public final static String MESSAGE_SYNC_ALERTS = "alert";
	public final static String MESSAGE_SYNC_FRIENDSHIPS = "friends";
	public final static String MESSAGE_SYNC_USER_DATA = "profile";

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		Log.e(TAG, messageType);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.e(TAG, "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.e(TAG, "Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// If it's a regular GCM message, do some work.
				Log.i(TAG, "Received: " + extras.toString());

				String message = extras.getString("message");
				Log.i(TAG, "message: " + message);

				if (message == null) {
					SyncUtils.syncAll(getApplicationContext());
				} else if (message.equals(MESSAGE_SYNC_ALERTS)) {
					SyncUtils.syncAlerts(getApplicationContext());
				} else if (message.equals(MESSAGE_SYNC_FRIENDSHIPS)) {
					SyncUtils.syncFriendships(getApplicationContext());
				} else if (message.equals(MESSAGE_SYNC_USER_DATA)) {
					SyncUtils.syncCurrentUserData(getApplicationContext());
				}
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}
