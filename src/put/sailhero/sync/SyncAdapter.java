package put.sailhero.sync;

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
			// TODO: do sth
		} else {
			// TODO: select data to sync from extras
		}

		RetrieveAlertsRequestHelper retrieveAlertsRequestHelper = new RetrieveAlertsRequestHelper(getContext());
		try {
			retrieveAlertsRequestHelper.doRequest();
			retrieveAlertsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrieveRegionsRequestHelper retrieveRegionsRequestHelper = new RetrieveRegionsRequestHelper(getContext());
		try {
			retrieveRegionsRequestHelper.doRequest();
			retrieveRegionsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrievePortsRequestHelper retrievePortsRequestHelper = new RetrievePortsRequestHelper(getContext());
		try {
			retrievePortsRequestHelper.doRequest();
			retrievePortsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrieveSentFriendshipsRequestHelper retrieveSentFriendshipsRequestHelper = new RetrieveSentFriendshipsRequestHelper(
				getContext());
		try {
			retrieveSentFriendshipsRequestHelper.doRequest();
			retrieveSentFriendshipsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrievePendingFriendshipsRequestHelper retrievePendingFriendshipsRequestHelper = new RetrievePendingFriendshipsRequestHelper(
				getContext());
		try {
			retrievePendingFriendshipsRequestHelper.doRequest();
			retrievePendingFriendshipsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrieveFriendshipsRequestHelper retrieveFriendshipsRequestHelper = new RetrieveFriendshipsRequestHelper(
				getContext());
		try {
			retrieveFriendshipsRequestHelper.doRequest();
			retrieveFriendshipsRequestHelper.storeData();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
