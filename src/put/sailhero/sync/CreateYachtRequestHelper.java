package put.sailhero.sync;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.exception.UnprocessableEntityException;
import put.sailhero.exception.YachtAlreadyCreatedException;
import put.sailhero.model.Yacht;
import put.sailhero.util.PrefUtils;
import android.content.Context;
import android.net.Uri;

public class CreateYachtRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_YACHTS = "yachts";

	private Yacht mSentYacht;
	private Yacht mRetrievedYacht;

	public CreateYachtRequestHelper(Context context, String name, Integer length, Integer width, Integer crew) {
		super(context);

		mSentYacht = new Yacht();
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
		Uri uri = API_BASE_URI.buildUpon().appendPath(PATH_YACHTS).build();

		mHttpUriRequest = new HttpPost(uri.toString());
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
		((HttpPost) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, UnprocessableEntityException,
			YachtAlreadyCreatedException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 201) {
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
		} else if (statusCode == 461) {
			throw new YachtAlreadyCreatedException();
		} else {
			throw new SystemException("Invalid status code");
		}
	}

	@Override
	public void storeData() {
		if (mRetrievedYacht == null) {
			PrefUtils.setYacht(mContext, null);
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
