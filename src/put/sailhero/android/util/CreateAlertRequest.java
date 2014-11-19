package put.sailhero.android.util;

import java.io.IOException;

import org.json.simple.JSONObject;

import put.sailhero.android.AccountUtils;
import put.sailhero.android.util.model.Alert;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.content.Context;
import android.location.Location;
import android.net.Uri;

public class CreateAlertRequest implements Request {
	
	private final static String CREATE_ALERT_REQUEST_PATH = "alerts";
	
	private SailHeroService service = SailHeroService.getInstance();
	private SailHeroSettings settings = service.getSettings();
	
	private Context mContext;
	
	private Alert alert;
	
	public CreateAlertRequest(Context context, String alertType, Location location, String additionalInfo) {
		mContext = context;
		
		alert = new Alert();
		alert.setAlertType(alertType);
		alert.setLocation(location);
		alert.setAdditionalInfo(additionalInfo);
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
		Account account = AccountUtils.getActiveAccount(mContext);
		AccountManager accountManager = AccountManager.get(mContext);
		
		String accessToken = "";
		try {
			accessToken = accountManager.blockingGetAuthToken(account, AccountUtils.ACCESS_TOKEN_TYPE, true);
		} catch (OperationCanceledException | AuthenticatorException | IOException e) {
			e.printStackTrace();
		}
		
		Header[] headers = new Header[] {
				new Header("Authorization", "Bearer " + accessToken),
				new Header("Content-Type", "application/json")
				};
		return headers;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public String getBody() {
		JSONObject obj = new JSONObject();
		
		JSONObject alertObject = alert.toJSONObject();		
		obj.put("alert", alertObject);
		
		return obj.toString();
	}
	
	@Override
	public Method getMethod() {
		return Method.POST;
	}
}
