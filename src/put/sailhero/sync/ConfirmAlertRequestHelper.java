package put.sailhero.sync;

import org.apache.http.client.methods.HttpPost;

import android.content.Context;
import android.net.Uri;

public class ConfirmAlertRequestHelper extends AlertResponseRequestHelper {

	public final static String TAG = "sailhero";

	private final static String PATH_CONFIRMATIONS = "confirmations";

	public ConfirmAlertRequestHelper(Context context, Integer alertId) {
		super(context, alertId);
	}

	@Override
	protected void createMethodClient() {
		Uri uri = API_BASE_URI.buildUpon()
				.appendPath(PATH_ALERTS)
				.appendPath(mSentId.toString())
				.appendPath(PATH_CONFIRMATIONS)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}
}
