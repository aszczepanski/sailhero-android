package put.sailhero.android.app;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.CreateYachtRequest;
import put.sailhero.android.utils.CreateYachtResponse;
import put.sailhero.android.utils.CreateYachtResponseCreator;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class CreateYachtAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private CreateYachtRequest mCreateYachtRequest;

	private Context mContext;
	private CreateYachtListener mCreateYachtListener;

	public CreateYachtAsyncTask(CreateYachtRequest createYachtRequest, Context context,
			CreateYachtListener createYachtListener) {
		mCreateYachtRequest = createYachtRequest;
		mContext = context;
		mCreateYachtListener = createYachtListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Creating yacht", "Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();
		Request request;

		request = mCreateYachtRequest;
		CreateYachtResponse createYachtResponse;
		try {
			createYachtResponse = connection.send(request, new CreateYachtResponseCreator());
			mSettings.setYacht(createYachtResponse.getYacht());
			mSettings.save();
			Log.d(TAG, "created yach with id " + mSettings.getYacht().getId());
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
			mCreateYachtListener.onYachtCreated();
		} else if (mException instanceof UnprocessableEntityException) {
			mCreateYachtListener.onUnprocessableEntityException(new YachtParametersErrorsHolder(
					mException.getMessage()));
		} else if (mException instanceof InvalidResourceOwnerException) {
			Toast.makeText(mContext, mException.getMessage(), Toast.LENGTH_LONG).show();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface CreateYachtListener {
		public void onYachtCreated();

		public void onUnprocessableEntityException(YachtParametersErrorsHolder errorsHolder);
	}
}
