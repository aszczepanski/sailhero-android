package put.sailhero.android;

import android.net.Uri;

public class AuthenticateUserRequest implements Request {
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	private String username;
	private String password;
	
	public AuthenticateUserRequest(String username, String password) {
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String getUrl() {
		final String authenticateUserHost = settings.getAccessTokenHost();
		final String authenticateUserPath = settings.getAccessTokenPath();
		
		Uri uri = new Uri.Builder()
		.scheme("http")
		.encodedAuthority(authenticateUserHost)
		.path(authenticateUserPath)
		.appendQueryParameter("client_id", settings.getAppId())
		.appendQueryParameter("client_secret", settings.getAppSecret())
		.appendQueryParameter("grant_type", "password")
		.appendQueryParameter("username", username)
		.appendQueryParameter("password", password)
		.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		// TODO Auto-generated method stub
		return null;
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
