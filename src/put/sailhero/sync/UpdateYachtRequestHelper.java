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

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.model.Yacht;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class UpdateYachtRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_YACHTS = "yachts";

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
		Uri uri = API_BASE_URI.buildUpon()
				.appendEncodedPath(PATH_YACHTS)
				.appendPath(mSentYacht.getId().toString())
				.build();

		mHttpUriRequest = new HttpPut(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
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
		PrefUtils.setYacht(mContext, mRetrievedYacht);
	}
}
