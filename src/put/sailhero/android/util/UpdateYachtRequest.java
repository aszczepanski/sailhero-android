package put.sailhero.android.util;

import org.json.simple.JSONObject;

import put.sailhero.android.util.model.Yacht;
import android.net.Uri;

public class UpdateYachtRequest extends YachtRequest {

	private final static String UPDATE_YACHT_REQUEST_PATH = "yachts";

	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();

	private Yacht yacht;

	public UpdateYachtRequest(Integer id, String name, Integer length, Integer width, Integer crew) {
		yacht = new Yacht();
		yacht.setId(id);
		yacht.setName(name);
		yacht.setLength(length);
		yacht.setWidth(width);
		yacht.setCrew(crew);
	}

	@Override
	public String getUrl() {
		final String apiHost = settings.getApiHost();
		final String apiPath = settings.getApiPath();
		final String version = settings.getVersion();
		final String i18n = settings.getI18n();

		Uri uri = new Uri.Builder().scheme("http").encodedAuthority(apiHost).appendPath(apiPath)
				.appendPath(version).appendPath(i18n).appendEncodedPath(UPDATE_YACHT_REQUEST_PATH)
				.appendPath(yacht.getId().toString()).build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		return new Header[] { new Header("Authorization", "Bearer " + settings.getAccessToken()),
				new Header("Content-Type", "application/json") };
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();

		JSONObject yachtObject = yacht.toJSONObject();
		obj.put("yacht", yachtObject);

		return obj.toString();
	}

	@Override
	public Method getMethod() {
		return Method.PUT;
	}

}
