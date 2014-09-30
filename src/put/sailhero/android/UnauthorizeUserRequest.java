package put.sailhero.android;

import android.net.Uri;

public class UnauthorizeUserRequest implements Request {
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	@Override
	public String getUrl() {
		final String unauthorizeUserHost = settings.getAccessTokenHost();
		final String unauthorizeUserPath = "oauth/revoke";
		
		Uri uri = new Uri.Builder()
		.scheme("http")
		.encodedAuthority(unauthorizeUserHost)
		.path(unauthorizeUserPath)
		.appendQueryParameter("token", settings.getAccessToken())
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
		return Method.POST;
	}
}
