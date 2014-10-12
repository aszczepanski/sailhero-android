package put.sailhero.android.app;

import put.sailhero.android.exception.TransportException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.SelectRegionRequest;
import put.sailhero.android.utils.SelectRegionResponse;
import put.sailhero.android.utils.SelectRegionResponseCreator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

class SelectRegionAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private SelectRegionRequest mSelectRegionRequest;

	private Context mContext;
	private SelectRegionListener mSelectRegionListener;

	public SelectRegionAsyncTask(SelectRegionRequest selectRegionRequest, Context context,
			SelectRegionListener selectRegionListener) {
		mSelectRegionRequest = selectRegionRequest;
		mContext = context;
		mSelectRegionListener = selectRegionListener;
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

		request = mSelectRegionRequest;
		SelectRegionResponse selectRegionResponse;
		try {
			selectRegionResponse = connection.send(request, new SelectRegionResponseCreator());
			mSettings.setRegion(selectRegionResponse.getRegion());
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
			mSelectRegionListener.onRegionSelected();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface SelectRegionListener {
		public void onRegionSelected();
	}
}
