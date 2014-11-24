package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Region;
import put.sailhero.model.User;
import put.sailhero.model.Yacht;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class RetrieveUserRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String USER_PROFILE_REQUEST_PATH = "users/me";

	private User mRetrievedUser;
	private Region mRetrievedRegion;
	private Yacht mRetrievedYacht;

	public RetrieveUserRequestHelper(Context context) {
		super(context);
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
				.appendEncodedPath(USER_PROFILE_REQUEST_PATH)
				.build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(EntityUtils.toString(mHttpResponse.getEntity()));

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
			} catch (IOException e) {
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
		if (mRetrievedUser == null) {
			PrefUtils.setRegion(mContext, null);
		} else {
			User oldUser = PrefUtils.getUser(mContext);
			if (oldUser == null || oldUser.getId() != mRetrievedUser.getId()
					|| oldUser.getEmail() != mRetrievedUser.getEmail() || oldUser.getName() != mRetrievedUser.getName()
					|| oldUser.getSurname() != mRetrievedUser.getSurname()) {
				PrefUtils.setUser(mContext, mRetrievedUser);
			}
		}

		if (mRetrievedYacht == null) {
			PrefUtils.setRegion(mContext, null);
		} else {
			Yacht oldYacht = PrefUtils.getYacht(mContext);
			if (oldYacht == null || oldYacht.getId() != mRetrievedYacht.getId()
					|| oldYacht.getName() != mRetrievedYacht.getName()
					|| oldYacht.getLength() != mRetrievedYacht.getLength()
					|| oldYacht.getWidth() != mRetrievedYacht.getWidth()
					|| oldYacht.getCrew() != mRetrievedYacht.getCrew()) {
				PrefUtils.setYacht(mContext, mRetrievedYacht);
			}

		}

		if (mRetrievedRegion == null) {
			PrefUtils.setRegion(mContext, null);
		} else {
			Region oldRegion = PrefUtils.getRegion(mContext);
			if (oldRegion == null || oldRegion.getId() != mRetrievedRegion.getId()
					|| oldRegion.getCodeName() != mRetrievedRegion.getCodeName()
					|| oldRegion.getFullName() != mRetrievedRegion.getFullName()) {
				PrefUtils.setRegion(mContext, mRetrievedRegion);
			}
		}
	}
}
