package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Region;
import put.sailhero.model.User;
import put.sailhero.model.Yacht;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.SyncUtils;
import android.content.Context;
import android.net.Uri;

public class RetrieveUserRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_USERS = "users";
	private final static String PATH_ME = "me";

	private User mRetrievedUser;
	private Region mRetrievedRegion;
	private Yacht mRetrievedYacht;

	public RetrieveUserRequestHelper(Context context) {
		super(context);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_USERS).appendPath(PATH_ME).build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONObject userObject = (JSONObject) obj.get("user");
				mRetrievedUser = new User(userObject);

				JSONObject yachtObject = (JSONObject) userObject.get("yacht");
				if (yachtObject != null) {
					mRetrievedYacht = new Yacht(yachtObject);
				} else {
					mRetrievedYacht = null;
				}

				JSONObject regionObject = (JSONObject) userObject.get("region");
				if (regionObject != null) {
					mRetrievedRegion = new Region(regionObject);
				} else {
					mRetrievedRegion = null;
				}

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			} catch (org.apache.http.ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}

	@Override
	public void storeData() throws SystemException {
		User oldUser = PrefUtils.getUser(mContext);
		PrefUtils.setUser(mContext, mRetrievedUser);

		Region oldRegion = PrefUtils.getRegion(mContext);
		PrefUtils.setRegion(mContext, mRetrievedRegion);

		PrefUtils.setYacht(mContext, mRetrievedYacht);

		if (oldUser != null && !mRetrievedRegion.getId().equals(oldRegion.getId())) {
			SyncUtils.syncAlerts(mContext);
			SyncUtils.syncPorts(mContext);
			SyncUtils.syncRoutes(mContext);
		}
	}
}
