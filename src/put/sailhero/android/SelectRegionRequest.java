package put.sailhero.android;

import android.net.Uri;

public class SelectRegionRequest implements Request {

	private final static String REGIONS_PATH = "regions";
	private final static String SELECT_PATH = "select";

	private SailHeroService service = SailHeroService.getInstance();
	private SailHeroSettings settings = service.getSettings();

	private Region region;

	public SelectRegionRequest(Region region) {
		this.region = region;
	}

	@Override
	public String getUrl() {
		final String apiHost = settings.getApiHost();
		final String apiPath = settings.getApiPath();
		final String version = settings.getVersion();
		final String i18n = settings.getI18n();

		Uri uri = new Uri.Builder().scheme("http").encodedAuthority(apiHost)
				.appendPath(apiPath).appendPath(version).appendPath(i18n)
				.appendEncodedPath(REGIONS_PATH)
				.appendEncodedPath(region.getId().toString())
				.appendEncodedPath(SELECT_PATH).build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[] { new Header("Authorization", "Bearer "
				+ settings.getAccessToken()) };
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
