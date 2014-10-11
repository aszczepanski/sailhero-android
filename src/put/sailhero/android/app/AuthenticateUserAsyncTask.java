package put.sailhero.android.app;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.utils.AuthenticateUserRequest;
import put.sailhero.android.utils.AuthenticateUserResponse;
import put.sailhero.android.utils.AuthenticateUserResponseCreator;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class AuthenticateUserAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private SailHeroSettings mSettings = mService.getSettings();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private AuthenticateUserRequest mAuthenticateUserRequest;

	private Context mContext;
	private AuthenticateUserListener mAuthenticateUserListener;

	public AuthenticateUserAsyncTask(AuthenticateUserRequest authenticateUserRequest,
			Context context, AuthenticateUserListener authenticateUserListener) {
		mAuthenticateUserRequest = authenticateUserRequest;
		mContext = context;
		mAuthenticateUserListener = authenticateUserListener;
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

		request = mAuthenticateUserRequest;
		AuthenticateUserResponse authenticateUserResponse;
		try {
			authenticateUserResponse = connection.send(request,
					new AuthenticateUserResponseCreator());
			mSettings.setAccessToken(authenticateUserResponse.getAccessToken());
			mSettings.save();
			Log.d(TAG, "access token: " + mSettings.getAccessToken());
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
			mAuthenticateUserListener.onUserAuthenticated();
		} else if (mException instanceof InvalidResourceOwnerException) {
			Toast.makeText(mContext, mException.getMessage(), Toast.LENGTH_LONG).show();
		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "Unknown error", Toast.LENGTH_LONG).show();
		}
	}

	public static interface AuthenticateUserListener {
		public void onUserAuthenticated();
	}
}
