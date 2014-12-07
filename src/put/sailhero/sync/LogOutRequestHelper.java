package put.sailhero.sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.json.simple.JSONObject;

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.util.AccountUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;

public class LogOutRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	public LogOutRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		final String unauthorizeUserHost = Config.ACCESS_TOKEN_HOST;
		final String unauthorizeUserPath = "oauth/revoke";

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(unauthorizeUserHost)
				.path(unauthorizeUserPath)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		Account account = AccountUtils.getActiveAccount(mContext);
		AccountManager accountManager = AccountManager.get(mContext);

		String accessToken = "";
		try {
			accessToken = accountManager.blockingGetAuthToken(account, AccountUtils.ACCESS_TOKEN_TYPE, true);
		} catch (OperationCanceledException | AuthenticatorException | IOException e) {
			e.printStackTrace();
		}

		JSONObject obj = new JSONObject();
		obj.put("token", accessToken);

		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString());
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		((HttpPost) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			// logged out
		} else {
			throw new SystemException("Invalid status code");
		}
	}
}
