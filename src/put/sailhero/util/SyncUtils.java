package put.sailhero.util;

import put.sailhero.Config;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.RequestHelper;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

public class SyncUtils {

	public static final String SYNC_EXTRAS_ITEMS_MASK = "put.sailhero.sync_items_mask_key";

	public static final int SYNC_ALERTS = 1 << 0;
	public static final int SYNC_FRIENDSHIPS = 1 << 1;
	public static final int SYNC_PORTS = 1 << 2;
	public static final int SYNC_REGIONS = 1 << 3;
	public static final int SYNC_CURRENT_USER_DATA = 1 << 4;
	public static final int SYNC_ROUTES = 1 << 5;

	private static void doOneAuthenticatedRequest(final Context context, RequestHelper requestHelper) throws Exception {
		try {
			requestHelper.doRequest();
			requestHelper.storeData();
		} catch (UnauthorizedException e) {
			AccountManager accountManager = AccountManager.get(context);
			Account account = AccountUtils.getActiveAccount(context);
			String accessToken = accountManager.peekAuthToken(account, AccountUtils.ACCESS_TOKEN_TYPE);
			accountManager.invalidateAuthToken(AccountUtils.ACCOUNT_TYPE, accessToken);

			Log.d(Config.TAG, "auth token invalidated");

			throw e;
		} catch (Exception e) {
			throw e;
		}
	}

	public static synchronized void doAuthenticatedRequest(final Context context, RequestHelper requestHelper)
			throws Exception {
		boolean authorizedRequest = false;
		Exception exception = null;

		do {
			try {
				doOneAuthenticatedRequest(context, requestHelper);
				authorizedRequest = true;
			} catch (UnauthorizedException e) {
				// try again
			} catch (Exception e) {
				exception = e;
				authorizedRequest = true;
			}
		} while (!authorizedRequest);

		if (exception != null) {
			throw exception;
		}
	}

	private static Bundle createSyncNowBundle() {
		Bundle bundle = new Bundle();

		// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

		return bundle;
	}

	private static void performSync(final Context context, int syncItemsMask) {
		// TODO
		Account account = AccountUtils.getActiveAccount(context);
		if (account != null) {
			ContentResolver.setSyncAutomatically(account, SailHeroContract.CONTENT_AUTHORITY, true);
			ContentResolver.setIsSyncable(account, SailHeroContract.CONTENT_AUTHORITY, 1);

			Bundle bundle = createSyncNowBundle();
			bundle.putInt(SYNC_EXTRAS_ITEMS_MASK, syncItemsMask);

			ContentResolver.requestSync(account, SailHeroContract.CONTENT_AUTHORITY, bundle);
		}
	}

	public static void syncAll(final Context context) {
		int syncItemsMask = SYNC_ALERTS | SYNC_FRIENDSHIPS | SYNC_PORTS | SYNC_REGIONS | SYNC_CURRENT_USER_DATA | SYNC_ROUTES;
		performSync(context, syncItemsMask);
	}

	public static void syncAlerts(final Context context) {
		int syncItemsMask = SYNC_ALERTS;
		performSync(context, syncItemsMask);
	}

	public static void syncFriendships(final Context context) {
		int syncItemsMask = SYNC_FRIENDSHIPS;
		performSync(context, syncItemsMask);
	}

	public static void syncRegions(final Context context) {
		int syncItemsMask = SYNC_REGIONS;
		performSync(context, syncItemsMask);
	}

	public static void syncPorts(final Context context) {
		int syncItemsMask = SYNC_PORTS;
		performSync(context, syncItemsMask);
	}

	public static void syncCurrentUserData(final Context context) {
		int syncItemsMask = SYNC_CURRENT_USER_DATA;
		performSync(context, syncItemsMask);
	}

	public static void syncRoutes(final Context context) {
		int syncItemsMask = SYNC_ROUTES;
		performSync(context, syncItemsMask);
	}
}
