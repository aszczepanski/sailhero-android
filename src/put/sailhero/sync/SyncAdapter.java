package put.sailhero.sync;

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.util.SyncUtils;
import android.accounts.Account;
import android.annotation.TargetApi;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

public class SyncAdapter extends AbstractThreadedSyncAdapter {

	public static final String TAG = "sailhero";

	private Handler mHandler;

	public SyncAdapter(Context context, boolean autoInitialize) {
		super(context, autoInitialize);

		mHandler = new Handler();
	}

	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
		super(context, autoInitialize, allowParallelSyncs);

		mHandler = new Handler();
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

		if ((syncItemsMask & SyncUtils.SYNC_ALERTS) > 0) {
			RetrieveAlertsRequestHelper retrieveAlertsRequestHelper = new RetrieveAlertsRequestHelper(getContext());
			performSyncAndHandleErrors(retrieveAlertsRequestHelper);
		}
		if ((syncItemsMask & SyncUtils.SYNC_FRIENDSHIPS) > 0) {
			RetrieveFriendshipsRequestHelper retrieveFriendshipsRequestHelper = new RetrieveFriendshipsRequestHelper(
					getContext());
			performSyncAndHandleErrors(retrieveFriendshipsRequestHelper);
		}
		if ((syncItemsMask & SyncUtils.SYNC_PORTS) > 0) {
			RetrievePortsRequestHelper retrievePortsRequestHelper = new RetrievePortsRequestHelper(getContext());
			performSyncAndHandleErrors(retrievePortsRequestHelper);
		}
		if ((syncItemsMask & SyncUtils.SYNC_REGIONS) > 0) {
			RetrieveRegionsRequestHelper retrieveRegionsRequestHelper = new RetrieveRegionsRequestHelper(getContext());
			performSyncAndHandleErrors(retrieveRegionsRequestHelper);
		}
		if ((syncItemsMask & SyncUtils.SYNC_USER_DATA) > 0) {
			RetrieveUserRequestHelper retrieveUserRequestHelper = new RetrieveUserRequestHelper(getContext());
			performSyncAndHandleErrors(retrieveUserRequestHelper);
		}
		if ((syncItemsMask & SyncUtils.SYNC_ROUTES) > 0) {
			RetrieveRoutesRequestHelper retrieveRoutesRequestHelper = new RetrieveRoutesRequestHelper(getContext());
			performSyncAndHandleErrors(retrieveRoutesRequestHelper);
		}
	}

	private void performSyncAndHandleErrors(final RequestHelper requestHelper) {
		try {
			SyncUtils.doAuthenticatedRequest(getContext(), requestHelper);
		} catch (InvalidRegionException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();

			final long delayMillis = 10000l;

			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					performSyncAndHandleErrors(requestHelper);
				}
			}, delayMillis);
		}
	}
}
