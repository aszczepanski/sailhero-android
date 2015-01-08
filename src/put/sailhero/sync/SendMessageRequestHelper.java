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

import put.sailhero.exception.InvalidRegionException;
import put.sailhero.exception.NotFoundException;
import put.sailhero.exception.SystemException;
import put.sailhero.exception.UnauthorizedException;
import put.sailhero.model.Message;
import android.content.Context;
import android.net.Uri;

public class SendMessageRequestHelper extends RequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_MESSAGES = "messages";

	private String mSentMessageBody;

	private Message mRetrievedMessage;

	public SendMessageRequestHelper(Context context, String messageBody) {
		super(context);
		mSentMessageBody = messageBody;
	}

	public String getSentMessageBody() {
		return mSentMessageBody;
	}

	public Message getRetrievedMessage() {
		return mRetrievedMessage;
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon().appendEncodedPath(PATH_MESSAGES).build();

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

		JSONObject messageObject = new JSONObject();
		messageObject.put("body", mSentMessageBody);
		obj.put("message", messageObject);

		HttpEntity entity = null;
		try {
			entity = new StringEntity(obj.toString(), CHARSET);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		((HttpPost) mHttpUriRequest).setEntity(entity);
	}

	@Override
	protected void parseResponse() throws SystemException, UnauthorizedException, NotFoundException,
			InvalidRegionException {
		int statusCode = mHttpResponse.getStatusLine().getStatusCode();
		String responseBody = "";
		try {
			responseBody = EntityUtils.toString(mHttpResponse.getEntity(), CHARSET);
		} catch (ParseException | IOException e) {
			e.printStackTrace();
		}

		if (statusCode == 201) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj;
				obj = (JSONObject) parser.parse(responseBody);

				JSONObject messageObject = (JSONObject) obj.get("message");
				mRetrievedMessage = new Message(messageObject);

			} catch (ParseException | org.json.simple.parser.ParseException | NullPointerException
					| NumberFormatException e) {
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
