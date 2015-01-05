package put.sailhero.util;

import put.sailhero.ui.LoginActivity;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class AccountUtils {

	public static final String ACCOUNT_TYPE = "put.sailhero.account";
	public static final String ACCESS_TOKEN_TYPE = "put.sailhero.token.access";
	public static final String REFRESH_TOKEN_TYPE = "put.sailhero.token.refresh";

	public static void addAccount(Context context, String userName, String accessToken, String refreshToken) {
		AccountManager accountManager = AccountManager.get(context);

		Account account = new Account(userName, ACCOUNT_TYPE);
		accountManager.addAccountExplicitly(account, null, null);
		// store access token instead of password (in order to use refresh token):
		accountManager.setPassword(account, accessToken);

		accountManager.setAuthToken(account, ACCESS_TOKEN_TYPE, accessToken);
		accountManager.setAuthToken(account, REFRESH_TOKEN_TYPE, refreshToken);
	}

	public static Account getActiveAccount(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account[] accountsArray = accountManager.getAccountsByType(ACCOUNT_TYPE);

		Account account = null;

		if (accountsArray != null && accountsArray.length > 0) {
			account = accountsArray[0];
		}

		return account;
	}

	public static void removeActiveAccount(Context context) {
		AccountManager accountManager = AccountManager.get(context);
		Account account = getActiveAccount(context);

		if (account != null) {
			accountManager.removeAccount(account, null, null);
		}
	}

	public static boolean finishActivityAndStartLoginActivityIfNeeded(Context context, Activity activity) {
		Account account = getActiveAccount(context);
		if (account == null) {
			Intent loginIntent = new Intent(context, LoginActivity.class);
			loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
			context.startActivity(loginIntent);
			activity.finish();
			return true;
		}
		return false;
	}

}
