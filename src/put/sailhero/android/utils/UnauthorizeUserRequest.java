package put.sailhero.android.utils;

import org.json.simple.JSONObject;

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
		// .appendQueryParameter("token", settings.getAccessToken())
		.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[] {
				new Header("Content-Type", "application/json"),
				new Header("Authorization", "Bearer " + settings.getAccessToken())
		};
	}

	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();
		obj.put("token", settings.getAccessToken());
		
		return obj.toString();
	}

	@Override
	public Method getMethod() {
		return Method.POST;
	}
}
