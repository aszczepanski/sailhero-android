package put.sailhero.android.app;

import put.sailhero.android.exception.TransportException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.UserProfileRequest;
import put.sailhero.android.utils.UserProfileResponse;
import put.sailhero.android.utils.UserProfileResponseCreator;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class GetUserProfileAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private UserProfileRequest mUserProfileRequest;

	private Context mContext;
	private GetUserProfileListener mGetUserProfileListener;

	public GetUserProfileAsyncTask(UserProfileRequest userProfileRequest, Context context,
			GetUserProfileListener getUserProfileListener) {
		mUserProfileRequest = userProfileRequest;
		mContext = context;
		mGetUserProfileListener = getUserProfileListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Getting user profile",
				"Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();
		Request request;

		request = mUserProfileRequest;
		UserProfileResponse userProfileResponse;
		try {
			userProfileResponse = connection.send(request, new UserProfileResponseCreator());

			mSettings.setUser(userProfileResponse.getUser());
			mSettings.setYacht(userProfileResponse.getYacht());
			mSettings.setRegion(userProfileResponse.getRegion());

			Log.d(TAG, "user: " + mSettings.getUser().getEmail());
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
			mGetUserProfileListener.onUserProfileReceived();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface GetUserProfileListener {
		public void onUserProfileReceived();
	}
}
