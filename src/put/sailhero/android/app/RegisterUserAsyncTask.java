package put.sailhero.android.app;

import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.CreateUserRequest;
import put.sailhero.android.utils.CreateUserResponse;
import put.sailhero.android.utils.CreateUserResponseCreator;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

class RegisterUserAsyncTask extends AsyncTask<Void, Void, Void> {

	public final static String TAG = "sailhero";

	private SailHeroService mService = SailHeroService.getInstance();
	private Exception mException;
	private ProgressDialog mProgressDialog;
	private CreateUserRequest mCreateUserRequest;

	private Context mContext;
	private RegisterUserListener mRegisterUserListener;

	public RegisterUserAsyncTask(CreateUserRequest createUserRequest, Context context,
			RegisterUserListener registerUserListener) {
		mCreateUserRequest = createUserRequest;
		mContext = context;
		mRegisterUserListener = registerUserListener;
	}

	@Override
	protected void onPreExecute() {
		mProgressDialog = ProgressDialog.show(mContext, "Registering user", "Executing request...");
	}

	@Override
	protected Void doInBackground(Void... params) {
		Connection connection = mService.getConnection();
		Request request;

		request = mCreateUserRequest;
		CreateUserResponse createUserResponse;
		try {
			createUserResponse = connection.send(request, new CreateUserResponseCreator());
			Log.d(TAG, "Created user with id: " + createUserResponse.getUser().getId());
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
			Toast.makeText(mContext, "User registered.", Toast.LENGTH_SHORT).show();
			mRegisterUserListener.onUserRegistered();
		} else if (mException instanceof UnprocessableEntityException) {
			Toast.makeText(mContext, mException.getMessage(), Toast.LENGTH_LONG).show();
			mRegisterUserListener
					.onUnprocessableEntityException(new RegisterUserEntityErrorsHolder(mException.getMessage()));

		} else if (mException instanceof TransportException) {
			Toast.makeText(mContext, "Service unavailable, try again later...", Toast.LENGTH_LONG)
					.show();
		} else {
			Toast.makeText(mContext, "An error has occured.", Toast.LENGTH_SHORT).show();
		}
	}

	public static interface RegisterUserListener {
		public void onUserRegistered();

		public void onUnprocessableEntityException(RegisterUserEntityErrorsHolder errorsHolder);
	}
}
