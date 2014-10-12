package put.sailhero.android.app;

import put.sailhero.android.exception.TransportException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.GetRegionsRequest;
import put.sailhero.android.utils.GetRegionsResponse;
import put.sailhero.android.utils.GetRegionsResponseCreator;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class GetRegionsAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private GetRegionsRequest mGetRegionsRequest;

	private Context mContext;
	private GetRegionsListener mGetRegionsListener;

	public GetRegionsAsyncTask(GetRegionsRequest getRegionsRequest, Context context,
			GetRegionsListener getRegionsListener) {
		mGetRegionsRequest = getRegionsRequest;
		mContext = context;
		mGetRegionsListener = getRegionsListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Authenticating user",
				"Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();
		Request request;

		request = mGetRegionsRequest;
		GetRegionsResponse getRegionsResponse;
		try {
			getRegionsResponse = connection.send(request, new GetRegionsResponseCreator());
			mSettings.setRegionsList(getRegionsResponse.getRegions());
		} catch (Exception e) {
			mException = e;
			e.printStackTrace();
		}

		return null;
	}

	@Override
	protected void onPostExecute(Void result) {
		mProgressDialog.dismiss();
		if (mException == null) {
			mGetRegionsListener.onRegionsReceived();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface GetRegionsListener {
		public void onRegionsReceived();
	}
}
