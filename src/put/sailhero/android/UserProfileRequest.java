package put.sailhero.android;

import android.net.Uri;

public class UserProfileRequest implements Request {
	
	private final static String USER_PROFILE_REQUEST_PATH = "api/v1/en/users/me";
	
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	@Override
	public String getUrl() {
		// String authenticateUserUrl = settings.getApiUrl();
		Uri uri;
		uri = new Uri.Builder()
		.scheme("http")
		.authority("sailhero-staging.herokuapp.com")
		.path(USER_PROFILE_REQUEST_PATH)
		.appendQueryParameter("access_token", settings.getAccessToken())
		.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return null;
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
