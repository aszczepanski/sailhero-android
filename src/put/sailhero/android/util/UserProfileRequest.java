package put.sailhero.android.util;

import java.io.IOException;

import put.sailhero.android.AccountUtils;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.net.Uri;

public class UserProfileRequest implements Request {

	private final static String USER_PROFILE_REQUEST_PATH = "users/me";

	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();

	private Context mContext;

	public UserProfileRequest(Context context) {
		mContext = context;
	}

	@Override
	public String getUrl() {
		final String apiHost = settings.getApiHost();
		final String apiPath = settings.getApiPath();
		final String version = settings.getVersion();
		final String i18n = settings.getI18n();

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(USER_PROFILE_REQUEST_PATH)
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

		return new Header[] { new Header("Authorization", "Bearer " + accessToken) };
	}

	@Override
	public String getBody() {
		return "";
	}

	@Override
	public Method getMethod() {
		return Method.GET;
	}
}
