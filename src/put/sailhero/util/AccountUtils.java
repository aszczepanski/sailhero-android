package put.sailhero.util;

import put.sailhero.provider.SailHeroContract;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;

public class AccountUtils {

	public static final String ACCOUNT_TYPE = "put.sailhero.account";
	public static final String ACCESS_TOKEN_TYPE = "put.sailhero.token.access";
	public static final String REFRESH_TOKEN_TYPE = "put.sailhero.token.refresh";

	public static void addAccount(Context context, String userName, String accessToken, String refreshToken) {
		AccountManager accountManager = AccountManager.get(context);

		Account account = new Account(userName, ACCOUNT_TYPE);
		accountManager.addAccountExplicitly(account, null, null);
		accountManager.setAuthToken(account, ACCESS_TOKEN_TYPE, accessToken);
		accountManager.setAuthToken(account, REFRESH_TOKEN_TYPE, refreshToken);
		// store refresh token instead of password:
		// accountManager.setPassword(account, refreshToken);
		
		ContentResolver.setIsSyncable(account, SailHeroContract.CONTENT_AUTHORITY, 1);
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

}
