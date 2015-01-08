package put.sailhero.sync;

import java.io.IOException;

import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Region;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class SelectRegionRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_REGIONS = "regions";
	private final static String PATH_SELECT = "select";

	private Integer mRegionId;

	private Region mRetrievedRegion;

	public SelectRegionRequestHelper(Context context, Integer regionId) {
		super(context);
		mRegionId = regionId;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendEncodedPath(PATH_REGIONS)
				.appendEncodedPath(mRegionId.toString())
				.appendEncodedPath(PATH_SELECT)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, NotFoundException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj;
				obj = (JSONObject) parser.parse(responseBody);

				JSONObject regionObject = (JSONObject) obj.get("region");
				mRetrievedRegion = new Region(regionObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 404) {
			throw new NotFoundException();
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() {
		PrefUtils.setRegion(mContext, mRetrievedRegion);
	}
}
