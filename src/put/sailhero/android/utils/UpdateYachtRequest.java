package put.sailhero.android.utils;

import org.json.simple.JSONObject;

import android.net.Uri;

public class UpdateYachtRequest extends YachtRequest {

	private final static String UPDATE_YACHT_REQUEST_PATH = "yachts";

	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();

	private Integer id;
	private String name;
	private Integer length;
	private Integer width;
	private Integer crew;

	public UpdateYachtRequest(Integer id, String name, Integer length, Integer width, Integer crew) {
		this.id = id;
		this.name = name;
		this.length = length;
		this.width = width;
		this.crew = crew;
	}

	@Override
	public String getUrl() {
		final String apiHost = settings.getApiHost();
		final String apiPath = settings.getApiPath();
		final String version = settings.getVersion();
		final String i18n = settings.getI18n();

		Uri uri = new Uri.Builder().scheme("http").encodedAuthority(apiHost).appendPath(apiPath)
				.appendPath(version).appendPath(i18n).appendEncodedPath(UPDATE_YACHT_REQUEST_PATH)
				.appendPath(id.toString()).build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[] { new Header("Authorization", "Bearer " + settings.getAccessToken()),
				new Header("Content-Type", "application/json") };
	}

	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();

		JSONObject yachtObject = new JSONObject();
		yachtObject.put("name", name);
		yachtObject.put("length", length.toString());
		yachtObject.put("width", width.toString());
		yachtObject.put("crew", crew.toString());

		obj.put("yacht", yachtObject);

		return obj.toString();
	}

	@Override
	public Method getMethod() {
		return Method.PUT;
	}

}
