package put.sailhero.sync;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Message;
import android.content.Context;
import android.net.Uri;
import android.util.Log;

public class RetrieveMessagesRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_MESSAGES = "messages";

	public final static String ORDER_ASC = "ASC";
	public final static String ORDER_DESC = "DESC";

	private Integer mSentLimit;
	private Integer mSentSince;
	private String mSentOrder;

	private LinkedList<Message> mRetrievedMessages;
	private Integer mRetrievedNextMessageId;

	public RetrieveMessagesRequestHelper(Context context, Integer limit, Integer since, String order) {
		super(context);

		mSentLimit = limit;
		mSentSince = since;
		mSentOrder = order;
	}

	public String getSentOrder() {
		return mSentOrder;
	}

	public LinkedList<Message> getRetrievedMessages() {
		return mRetrievedMessages;
	}

	public Integer getRetrievedNextMessageId() {
		return mRetrievedNextMessageId;
	}

	@Override
	protected void createMethodClient() {
		Uri.Builder uriBuilder = API_BASE_URI.buildUpon().appendPath(PATH_MESSAGES);
		if (mSentLimit != null) {
			uriBuilder.appendQueryParameter("limit", mSentLimit.toString());
		}
		if (mSentSince != null) {
			uriBuilder.appendQueryParameter("since", mSentSince.toString());
		}
		if (mSentOrder != null) {
			uriBuilder.appendQueryParameter("order", mSentOrder);
		}
		Uri uri = uriBuilder.build();

		mHttpUriRequest = new HttpGet(uri.toString());
	}

	@Override
	protected void setHeaders() {
		addHeaderAuthorization();
		addHeaderPosition();
	}

	@Override
	protected void parseResponse() throws UnauthorizedException, SystemException, InvalidRegionException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 200) {
			try {
				Log.e(TAG, responseBody);

				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(responseBody);

				mRetrievedMessages = new LinkedList<Message>();

				JSONArray messagesArray = (JSONArray) obj.get("messages");
				for (int i = 0; i < messagesArray.size(); i++) {
					JSONObject messageObject = (JSONObject) messagesArray.get(i);
					Message message = new Message(messageObject);

					mRetrievedMessages.addLast(message);

					Log.i(TAG, messageObject.toString());
				}

				Object nextMessageObject = obj.get("next");
				if (nextMessageObject != null) {
					mRetrievedNextMessageId = Integer.valueOf(nextMessageObject.toString());
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
		} else if (statusCode == 460) {
			throw new InvalidRegionException();
		} else {
			throw new SystemException("Invalid status code (" + statusCode + ")");
		}
	}
}
