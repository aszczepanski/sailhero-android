package put.sailhero.android.util;

import org.json.simple.JSONObject;

import android.net.Uri;
import android.os.Build;

public class RegisterGcmRequest implements Request {

	private final static String REGISTER_GCM_REQUEST_PATH = "users/me/devices";

	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();

	String registrationId;
	String brand = Build.BRAND;
	String model = Build.MODEL;

	public RegisterGcmRequest(String registrationId) {
		this.registrationId = registrationId;
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
				.appendEncodedPath(REGISTER_GCM_REQUEST_PATH)
				.build();

		return uri.toString();
	}

	@Override
	public Header[] getHeaders() {
		Header[] headers = new Header[] {
				new Header("Authorization", "Bearer " + settings.getAccessToken()),
				new Header("Content-Type", "application/json") };
		return headers;
	}

	@SuppressWarnings("unchecked")
	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();

		obj.put("device_type", "ANDROID");
		obj.put("name", getDeviceName());
		obj.put("key", registrationId);

		return obj.toString();
	}

	@Override
	public Method getMethod() {
		return Method.POST;
	}

	private String getDeviceName() {
		return Build.BRAND + " " + Build.MODEL;
	}
}
