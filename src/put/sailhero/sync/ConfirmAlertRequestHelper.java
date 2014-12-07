package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.Config;
import put.sailhero.exception.ForbiddenException;
import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Alert;
import android.content.Context;
import android.net.Uri;

public class ConfirmAlertRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String ALERTS_REQUEST_PATH = "alerts";
	private final static String CONFIRMATIONS_REQUEST_PATH = "confirmations";

	private Integer mSentId;
	private Alert mRetrievedAlert;

	public ConfirmAlertRequestHelper(Context context, Integer alertId) {
		super(context);

		mSentId = alertId;
	}

	public Integer getSentId() {
		return mSentId;
	}

	public Alert getRetrievedAlert() {
		return mRetrievedAlert;
	}

	@Override
	protected void createMethodClient() {
		final String apiHost = Config.API_HOST;
		final String apiPath = Config.API_PATH;
		final String version = Config.VERSION;
		final String i18n = Config.I18N;

		Uri uri = new Uri.Builder().scheme("http")
				.encodedAuthority(apiHost)
				.appendPath(apiPath)
				.appendPath(version)
				.appendPath(i18n)
				.appendEncodedPath(ALERTS_REQUEST_PATH)
				.appendPath(mSentId.toString())
				.appendPath(CONFIRMATIONS_REQUEST_PATH)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, ForbiddenException,
			NotFoundException, InvalidRegionException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONObject alertObject = (JSONObject) obj.get("alert");
				mRetrievedAlert = new Alert(alertObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 403) {
			throw new ForbiddenException();
		} else if (statusCode == 404) {
			throw new NotFoundException();
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else {
			throw new SystemException("Invalid status code(" + statusCode + ")");
		}
	}

	@Override
	public void storeData() {
		// TODO: save using content resolver
	}
}
