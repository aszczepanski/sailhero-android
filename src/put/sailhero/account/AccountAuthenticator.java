package put.sailhero.account;

import put.sailhero.ui.LoginActivity;
import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class AccountAuthenticator extends AbstractAccountAuthenticator {

	public final static String TAG = "sailhero";

	Context mContext;

	public AccountAuthenticator(Context context) {
		super(context);
		mContext = context;
	}

	@Override
	public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAccountRemovalAllowed(AccountAuthenticatorResponse response, Account account)
			throws NetworkErrorException {
		Log.d(TAG, "removal");

		Bundle bundle = new Bundle();
		bundle.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, true);
		return bundle;
	}

	@Override
	public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
			String authTokenType, String[] requiredFeatures, Bundle options)
			throws NetworkErrorException {
		Log.d(TAG, "accountType: " + accountType);
		Log.d(TAG, "authTokenType: " + authTokenType);

		final Intent intent = new Intent(mContext, LoginActivity.class);
		//		intent.putExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE, accountType);
		//		intent.putExtra(AuthenticatorActivity.ARG_AUTH_TYPE, authTokenType);
		//		intent.putExtra(AuthenticatorActivity.ARG_IS_ADDING_NEW_ACCOUNT, true);
		intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
		final Bundle bundle = new Bundle();
		bundle.putParcelable(AccountManager.KEY_INTENT, intent);
		return bundle;
	}

	@Override
	public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
			Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
			String authTokenType, Bundle options) throws NetworkErrorException {
		final AccountManager accountManager = AccountManager.get(mContext);
		String accessToken = accountManager.peekAuthToken(account, authTokenType);

		if (!TextUtils.isEmpty(accessToken)) {
			final Bundle result = new Bundle();
			result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
			result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
			result.putString(AccountManager.KEY_AUTHTOKEN, accessToken);
			return result;
		}

		String refreshToken = accountManager.getPassword(account);
		// TODO: try to refresh access token

		final Bundle result = new Bundle();
		result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
		result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
		result.putString(AccountManager.KEY_AUTHTOKEN, "xxx");
		return result;
	}

	@Override
	public String getAuthTokenLabel(String authTokenType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
			String authTokenType, Bundle options) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
			String[] features) throws NetworkErrorException {
		// TODO Auto-generated method stub
		return null;
	}

}
