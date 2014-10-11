package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.utils.AuthenticateUserRequest;
import put.sailhero.android.utils.AuthenticateUserResponse;
import put.sailhero.android.utils.AuthenticateUserResponseCreator;
import put.sailhero.android.utils.Connection;
import put.sailhero.android.utils.Request;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {

	public final static String TAG = "sailhero";

	private final static String APPLICATION_ID = "f69eaae2c402d352f15e57d928f839486232ffbafcb283f96b5a645e5db6e4b9";
	private final static String APPLICATION_SECRET = "0dc726a7ae4534274edd6d4ab8add076a25e488ebbba1ef99333bb18f909a186";
	private final static String ACCESS_TOKEN_HOST = "192.168.0.105:3000";
	private final static String API_HOST = "192.168.0.105:3000";

	/*
	 * private final static String APPLICATION_ID = "ad321fbef5954d26f6f5f83af54bc069533de674eb1d1f206cadead6c8dfcfa4";
	 * private final static String APPLICATION_SECRET = "ae93fa9a83beda667e9804e6d24fa9c05cb200ec9311a27719338d09a1608593";
	 * private final static String ACCESS_TOKEN_HOST = "sailhero-staging.herokuapp.com";
	 * private final static String API_HOST = "sailhero-staging.herokuapp.com";
	 */

	private final static String ACCESS_TOKEN_PATH = "oauth/token";
	private final static String API_PATH = "api";
	private final static String VERSION = "v1";
	private final static String I18N = "en";

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private Button mAuthenticateUserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		mService = SailHeroService.initialize(getApplicationContext());
		mSettings = mService.getSettings();

		mSettings.setAppId(APPLICATION_ID);
		mSettings.setAppSecret(APPLICATION_SECRET);
		mSettings.setAccessTokenHost(ACCESS_TOKEN_HOST);
		mSettings.setAccessTokenPath(ACCESS_TOKEN_PATH);
		mSettings.setApiHost(API_HOST);
		mSettings.setApiPath(API_PATH);
		mSettings.setVersion(VERSION);
		mSettings.setI18n(I18N);

		if (mSettings.getAccessToken() != null) {
			onConnected();
		}

		mAuthenticateUserButton = (Button) findViewById(R.id.ActivityLoginAuthenticateUserButton);
		mAuthenticateUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailText = (EditText) findViewById(R.id.ActivityLoginEmailEditText);
				EditText passwordText = (EditText) findViewById(R.id.ActivityLoginPasswordEditText);
				AuthenticateUserRequest authenticateUserRequest = new AuthenticateUserRequest(emailText
						.getText().toString().trim(), passwordText.getText().toString());
				AuthenticateUserTask task = new AuthenticateUserTask(authenticateUserRequest);
				task.execute();
			}
		});
	}

	@Override
	protected void onResume() {
		if (mSettings.getAccessToken() != null) {
			onConnected();
		}

		super.onResume();
	}

	private void onConnected() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	class AuthenticateUserTask extends AsyncTask<Void, Void, Void> {

		private SailHeroService mService = SailHeroService.getInstance();
		private Exception mException;
		ProgressDialog mProgressDialog;
		AuthenticateUserRequest mAuthenticateUserRequest;

		public AuthenticateUserTask(AuthenticateUserRequest authenticateUserRequest) {
			mAuthenticateUserRequest = authenticateUserRequest;
		}

		@Override
		protected void onPreExecute() {
			mProgressDialog = ProgressDialog.show(LoginActivity.this, "Authenticating user",
					"Executing request...");
		}

		@Override
		protected Void doInBackground(Void... params) {
			Connection connection = mService.getConnection();
			Request request;

			request = mAuthenticateUserRequest;
			AuthenticateUserResponse authenticateUserResponse;
			try {
				authenticateUserResponse = connection.send(request, new AuthenticateUserResponseCreator());
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
				onConnected();
			} else if (mException instanceof InvalidResourceOwnerException) {
				Toast.makeText(LoginActivity.this, mException.getMessage(), Toast.LENGTH_LONG).show();
			} else {
				Toast.makeText(LoginActivity.this, "An error has occured.", Toast.LENGTH_LONG).show();
			}
		}
	}
}
