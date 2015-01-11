package put.sailhero.gcm;

import put.sailhero.Config;
import put.sailhero.sync.RegisterGcmRequestHelper;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.SyncUtils;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class GcmRegistrationAsyncTask extends AsyncTask<Void, Void, Void> {

	private Context mContext;

	private GoogleCloudMessaging mGcm;
	private String mRegistrationId;

	private Exception mException;

	public GcmRegistrationAsyncTask(Context context) {
		mContext = context;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			if (mGcm == null) {
				mGcm = GoogleCloudMessaging.getInstance(mContext);
			}
			mRegistrationId = mGcm.register(Config.GCM_SENDER_ID);

			RegisterGcmRequestHelper requestHelper = new RegisterGcmRequestHelper(mContext, mRegistrationId);
			SyncUtils.doAuthenticatedRequest(mContext, requestHelper);

			PrefUtils.setGcmRegistrationId(mContext, mRegistrationId);
		} catch (Exception ex) {
			mException = ex;
		}
		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);

		if (mException == null) {
			Toast.makeText(mContext, "Gcm key registered.", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(mContext, "Error: " + mException.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
}
