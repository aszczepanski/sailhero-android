package put.sailhero.sync;

import org.apache.http.client.methods.HttpPost;

import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
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
				.appendEncodedPath(PATH_ALERTS)
				.appendPath(mSentId.toString())
				.appendPath(PATH_CONFIRMATIONS)
				.build();

		mHttpUriRequest = new HttpPost(uri.toString());
	}

	@Override
	public void storeData() {
		if (mRetrievedAlert == null) {
			return;
		}

		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Alert.COLUMN_NAME_RESPONSE_STATUS, SailHeroContract.Alert.RESPONSE_STATUS_CONFIRMED);

		mContext.getContentResolver().update(
				SailHeroContract.Alert.CONTENT_URI.buildUpon().appendPath(mRetrievedAlert.getId().toString()).build(),
				values, null, null);
	}
}
