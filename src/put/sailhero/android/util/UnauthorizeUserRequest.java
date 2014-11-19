package put.sailhero.android.util;

import java.io.IOException;

import org.json.simple.JSONObject;

import put.sailhero.android.AccountUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;

public class UnauthorizeUserRequest implements Request {
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();

	private Context mContext;

	public UnauthorizeUserRequest(Context context) {
		mContext = context;
	}

	@Override
	public String getUrl() {
		final String unauthorizeUserHost = settings.getAccessTokenHost();
		final String unauthorizeUserPath = "oauth/revoke";

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(unauthorizeUserHost)
				.path(unauthorizeUserPath)
				// .appendQueryParameter("token", settings.getAccessToken())
				.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		Account account = AccountUtils.getActiveAccount(mContext);
		AccountManager accountManager = AccountManager.get(mContext);

		String accessToken = "";
		try {
			accessToken = accountManager.blockingGetAuthToken(account,
					AccountUtils.ACCESS_TOKEN_TYPE, true);
		} catch (OperationCanceledException | AuthenticatorException | IOException e) {
			e.printStackTrace();
		}

		return new Header[] { new Header("Content-Type", "application/json"),
				new Header("Authorization", "Bearer " + accessToken) };
	}

	@Override
	public String getBody() {
		Account account = AccountUtils.getActiveAccount(mContext);
		AccountManager accountManager = AccountManager.get(mContext);

		String accessToken = "";
		try {
			accessToken = accountManager.blockingGetAuthToken(account,
					AccountUtils.ACCESS_TOKEN_TYPE, true);
		} catch (OperationCanceledException | AuthenticatorException | IOException e) {
			e.printStackTrace();
		}

		JSONObject obj = new JSONObject();
		obj.put("token", accessToken);

		return obj.toString();
	}

	@Override
	public Method getMethod() {
		return Method.POST;
	}
}
