package put.sailhero.sync;

import put.sailhero.exception.InvalidResourceOwnerException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.TransportException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
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
		} catch (TransportException | UnauthorizedException | SystemException | UnprocessableEntityException
				| InvalidResourceOwnerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrieveRegionsRequestHelper retrieveRegionsRequestHelper = new RetrieveRegionsRequestHelper(getContext());
		try {
			retrieveRegionsRequestHelper.doRequest();
			retrieveRegionsRequestHelper.storeData();
		} catch (TransportException | UnauthorizedException | SystemException | UnprocessableEntityException
				| InvalidResourceOwnerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		RetrievePortsRequestHelper retrievePortsRequestHelper = new RetrievePortsRequestHelper(getContext());
		try {
			retrievePortsRequestHelper.doRequest();
			retrievePortsRequestHelper.storeData();
		} catch (TransportException | UnauthorizedException | SystemException | UnprocessableEntityException
				| InvalidResourceOwnerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
