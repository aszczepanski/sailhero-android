package put.sailhero.sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.Config;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.model.Yacht;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class UpdateYachtRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String UPDATE_YACHT_REQUEST_PATH = "yachts";

	private Yacht mSentYacht;
	private Yacht mRetrievedYacht;

	public UpdateYachtRequestHelper(Context context, Integer id, String name, Integer length, Integer width,
			Integer crew) {
		super(context);

		mSentYacht = new Yacht();
		mSentYacht.setId(id);
		mSentYacht.setName(name);
		mSentYacht.setLength(length);
		mSentYacht.setWidth(width);
		mSentYacht.setCrew(crew);
	}

	public Yacht getSentYacht() {
		return mSentYacht;
	}

	public Yacht getRetrievedYacht() {
		return mRetrievedYacht;
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
				.appendEncodedPath(UPDATE_YACHT_REQUEST_PATH)
				.appendPath(mSentYacht.getId().toString())
				.build();

		mHttpUriRequest = new HttpPut(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderContentJson();
	}

	@Override
	protected void setEntity() {
		JSONObject obj = new JSONObject();

		JSONObject yachtObject = mSentYacht.toJSONObject();
		obj.put("yacht", yachtObject);

		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString());
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((HttpPut) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, UnprocessableEntityException {
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

				JSONObject yachtObject = (JSONObject) obj.get("yacht");
				mRetrievedYacht = new Yacht(yachtObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 422) {
			throw new UnprocessableEntityException(responseBody);
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() {
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
	}
}
