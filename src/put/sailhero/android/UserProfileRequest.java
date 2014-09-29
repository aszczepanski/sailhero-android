package put.sailhero.android;

import android.net.Uri;

public class UserProfileRequest implements Request {
	
	private final static String USER_PROFILE_REQUEST_PATH = "users/me";
	
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	@Override
	public String getUrl() {
		final String apiHost = settings.getApiHost();
		final String apiPath = settings.getApiPath();
		final String version = settings.getVersion();
		final String i18n = settings.getI18n();
		
		Uri uri = new Uri.Builder()
		.scheme("http")
		.encodedAuthority(apiHost)
		.appendPath(apiPath)
		.appendPath(version)
		.appendPath(i18n)
		.appendEncodedPath(USER_PROFILE_REQUEST_PATH)
		.appendQueryParameter("access_token", settings.getAccessToken())
		.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[0];
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
