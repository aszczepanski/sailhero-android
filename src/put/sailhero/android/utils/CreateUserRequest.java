package put.sailhero.android.utils;

import org.json.simple.JSONObject;

import android.net.Uri;

public class CreateUserRequest implements Request {
	
	private final static String CREATE_USER_REQUEST_PATH = "users";
	
	SailHeroService service = SailHeroService.getInstance();
	SailHeroSettings settings = service.getSettings();
	
	String email, password, passwordConfirmation, name, surname;
	
	public CreateUserRequest(String username, String password, String passwordConfirmation, String name, String surname) {
		this.email = username;
		this.password = password;
		this.passwordConfirmation = passwordConfirmation;
		this.name = name;
		this.surname = surname;
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
		.appendEncodedPath(CREATE_USER_REQUEST_PATH)
		.build();

		return uri.toString();
	}
	
	@Override
	public Header[] getHeaders() {
		Header[] headers = new Header[] {
				new Header("Content-Type", "application/json")
				};
		return headers;
	}
	
	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();
		
		JSONObject userObject = new JSONObject();
		userObject.put("email", email);
		userObject.put("password", password);
		userObject.put("password_confirmation", passwordConfirmation);
		userObject.put("name", name);
		userObject.put("surname", surname);
		
		obj.put("user", userObject);
		
		return obj.toString();
	}
	
	@Override
	public Method getMethod() {
		return Method.POST;
	}
}
