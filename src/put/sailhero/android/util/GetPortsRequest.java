package put.sailhero.android.util;

import android.net.Uri;

public class GetPortsRequest implements Request {

	private final static String MAP_PATH = "map";
	private final static String GET_PORTS_REQUEST_PATH = "ports";

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
		.appendEncodedPath(MAP_PATH)
		.appendEncodedPath(GET_PORTS_REQUEST_PATH)
		.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[] { new Header("Authorization", "Bearer " + settings.getAccessToken()) };
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
