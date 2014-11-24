package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Region;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class SelectRegionRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String REGIONS_PATH = "regions";
	private final static String SELECT_PATH = "select";

	private Integer mRegionId;

	private Region mRetrievedRegion;

	public SelectRegionRequestHelper(Context context, Integer regionId) {
		super(context);
		mRegionId = regionId;
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
				.appendEncodedPath(REGIONS_PATH)
				.appendEncodedPath(mRegionId.toString())
				.appendEncodedPath(SELECT_PATH)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj;
				obj = (JSONObject) parser.parse(EntityUtils.toString(mHttpResponse.getEntity()));

				JSONObject regionObject = (JSONObject) obj.get("region");
				mRetrievedRegion = new Region(regionObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException | IOException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() {
		Region oldRegion = PrefUtils.getRegion(mContext);
		if (oldRegion == null || oldRegion.getId() != mRetrievedRegion.getId()
				|| oldRegion.getCodeName() != mRetrievedRegion.getCodeName()
				|| oldRegion.getFullName() != mRetrievedRegion.getFullName()) {
			PrefUtils.setRegion(mContext, mRetrievedRegion);
		}
	}
}
