package put.sailhero.android.utils;

import org.json.simple.JSONObject;

import android.location.Location;
import android.net.Uri;

public class CreateAlertRequest implements Request {
	
	private final static String CREATE_ALERT_REQUEST_PATH = "alerts";
	
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	String alertType;
	Location location;
	String additionalInfo;
	
	public CreateAlertRequest(String alertType, Location location, String additionalInfo) {
		this.alertType = alertType;
		this.location = location;
		this.additionalInfo = additionalInfo;
	}
	
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
		.appendEncodedPath(CREATE_ALERT_REQUEST_PATH)
		.build();

		return uri.toString();
	}
	
	@Override
	public Header[] getHeaders() {
		Header[] headers = new Header[] {
				new Header("Authorization", "Bearer " + settings.getAccessToken()),
				new Header("Content-Type", "application/json")
				};
		return headers;
	}
	
	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();
		
		JSONObject alertObject = new JSONObject();
		alertObject.put("alert_type", alertType);
		alertObject.put("latitude", location.getLatitude());
		alertObject.put("longitude", location.getLongitude());
		alertObject.put("additional_info", additionalInfo);
		
		obj.put("alert", alertObject);
		
		return obj.toString();
	}
	
	@Override
	public Method getMethod() {
		return Method.POST;
	}
}
