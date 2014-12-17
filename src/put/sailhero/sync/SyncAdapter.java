package put.sailhero.sync;

import put.sailhero.util.SyncUtils;
import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	public static final String TAG = "sailhero";

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);
	}

	@Override
	public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider,
			SyncResult syncResult) {
		Log.i(TAG, "Beginning network synchronization");

		if (extras == null) {
			extras = new Bundle();
			extras.putInt(SyncUtils.SYNC_EXTRAS_ITEMS_MASK, SyncUtils.SYNC_ALERTS | SyncUtils.SYNC_FRIENDSHIPS
					| SyncUtils.SYNC_PORTS | SyncUtils.SYNC_REGIONS | SyncUtils.SYNC_USER_DATA);
		}

		int syncItemsMask = extras.getInt(SyncUtils.SYNC_EXTRAS_ITEMS_MASK);

		if (syncItemsMask == 0) {
			Log.e(TAG, "syncItemsMask is 0");
			return;
		}

		// TODO: make separate functions
		if ((syncItemsMask & SyncUtils.SYNC_ALERTS) > 0) {
			RetrieveAlertsRequestHelper retrieveAlertsRequestHelper = new RetrieveAlertsRequestHelper(getContext());
			try {
				SyncUtils.doAuthenticatedRequest(getContext(), retrieveAlertsRequestHelper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((syncItemsMask & SyncUtils.SYNC_FRIENDSHIPS) > 0) {
			RetrieveFriendshipsRequestHelper retrieveFriendshipsRequestHelper = new RetrieveFriendshipsRequestHelper(
					getContext());
			try {
				SyncUtils.doAuthenticatedRequest(getContext(), retrieveFriendshipsRequestHelper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((syncItemsMask & SyncUtils.SYNC_PORTS) > 0) {
			RetrievePortsRequestHelper retrievePortsRequestHelper = new RetrievePortsRequestHelper(getContext());
			try {
				SyncUtils.doAuthenticatedRequest(getContext(), retrievePortsRequestHelper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((syncItemsMask & SyncUtils.SYNC_REGIONS) > 0) {
			RetrieveRegionsRequestHelper retrieveRegionsRequestHelper = new RetrieveRegionsRequestHelper(getContext());
			try {
				SyncUtils.doAuthenticatedRequest(getContext(), retrieveRegionsRequestHelper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if ((syncItemsMask & SyncUtils.SYNC_USER_DATA) > 0) {
			RetrieveUserRequestHelper retrieveUserRequestHelper = new RetrieveUserRequestHelper(getContext());
			try {
				SyncUtils.doAuthenticatedRequest(getContext(), retrieveUserRequestHelper);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
