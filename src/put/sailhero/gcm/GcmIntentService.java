package put.sailhero.gcm;

import put.sailhero.account.AccountUtils;
import android.accounts.Account;
import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmIntentService extends IntentService {

	public final static String TAG = "sailhero";

	public GcmIntentService() {
		super("GcmIntentService");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				Log.e(TAG, "Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				Log.e(TAG, "Deleted messages on server: " + extras.toString());
				// If it's a regular GCM message, do some work.
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
				// Post notification of received message.
				Log.i(TAG, "Received: " + extras.toString());
				
				Account account = AccountUtils.getActiveAccount(getApplicationContext());				
				if (account != null) {
					Bundle bundle = new Bundle();
					// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
					bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
					bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

					ContentResolver.requestSync(account, "put.sailhero", bundle);
				}
			}
		}

		GcmBroadcastReceiver.completeWakefulIntent(intent);
	}
}
