package put.sailhero.android.app;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.CreateAlertRequest;
import put.sailhero.android.utils.CreateAlertResponse;
import put.sailhero.android.utils.CreateAlertResponseCreator;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class CreateAlertAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private CreateAlertRequest mCreateAlertRequest;

	private Context mContext;
	private CreateAlertListener mCreateAlertListener;

	public CreateAlertAsyncTask(CreateAlertRequest createAlertRequest, Context context,
			CreateAlertListener createAlertListener) {
		mCreateAlertRequest = createAlertRequest;
		mContext = context;
		mCreateAlertListener = createAlertListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Creating yacht", "Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();
		Request request;

		request = mCreateAlertRequest;
		CreateAlertResponse createAlertResponse;
		try {
			createAlertResponse = connection.send(request, new CreateAlertResponseCreator());
			mSettings.save();
			Log.d(TAG, "created yach with id " + createAlertResponse.getAlert().getId());
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
			mCreateAlertListener.onAlertCreated();
		} else if (mException instanceof UnprocessableEntityException) {
			// mCreateAlertListener.onUnprocessableEntityException(new
			// YachtParametersErrorsHolder(
			// mException.getMessage()));
		} else if (mException instanceof InvalidResourceOwnerException) {
			Toast.makeText(mContext, mException.getMessage(), Toast.LENGTH_LONG).show();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface CreateAlertListener {
		public void onAlertCreated();

		// public void onUnprocessableEntityException();
	}
}
