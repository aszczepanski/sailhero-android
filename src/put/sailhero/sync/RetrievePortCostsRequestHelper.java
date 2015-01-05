package put.sailhero.sync;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.NoSpotException;
import put.sailhero.exception.NoYachtException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RetrievePortCostsRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_MAP = "map";
	private final static String PATH_PORTS = "ports";
	private final static String PATH_CALCULATE = "calculate";

	private Integer mSentPortId;
	private Integer mRetrievedCost;
	private String mRetrievedCurrency;

	private LinkedList<String> mRetrievedMessages;

	public RetrievePortCostsRequestHelper(Context context, Integer portId) {
		super(context);

		mSentPortId = portId;
	}

	public Integer getSentPortId() {
		return mSentPortId;
	}

	public Integer getRetrievedCost() {
		return mRetrievedCost;
	}

	public String getRetrievedCurrency() {
		return mRetrievedCurrency;
	}

	public LinkedList<String> getRetrievedMessages() {
		return mRetrievedMessages;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_MAP)
				.appendPath(PATH_PORTS)
				.appendPath(mSentPortId.toString())
				.appendPath(PATH_CALCULATE)
				.build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException, NotFoundException,
			InvalidRegionException, NoSpotException, NoYachtException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity());
		} catch (IOException e) {
			e.printStackTrace();
		}

		Log.e(TAG, responseBody);

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				JSONObject portCostObject = (JSONObject) obj.get("port");

				mRetrievedCost = Integer.valueOf((String) portCostObject.get("cost"));
				mRetrievedCurrency = (String) portCostObject.get("currency");

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
		} else if (statusCode == 404) {
			throw new NotFoundException();
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else if (statusCode == 464) {
			throw new NoSpotException();
		} else if (statusCode == 465) {
			throw new NoYachtException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}
}
