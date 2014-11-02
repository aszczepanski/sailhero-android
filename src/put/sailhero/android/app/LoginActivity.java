package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.util.AuthenticateUserRequest;
import put.sailhero.android.util.AuthenticateUserResponse;
import put.sailhero.android.util.AuthenticateUserResponseCreator;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import android.app.Activity;
import android.content.Intent;
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
	private final static String ACCESS_TOKEN_HOST = "192.168.0.106:3000";
	private final static String API_HOST = "192.168.0.106:3000";

	/*
	 * private final static String APPLICATION_ID =
	 * "ad321fbef5954d26f6f5f83af54bc069533de674eb1d1f206cadead6c8dfcfa4";
	 * private final static String APPLICATION_SECRET =
	 * "ae93fa9a83beda667e9804e6d24fa9c05cb200ec9311a27719338d09a1608593";
	 * private final static String ACCESS_TOKEN_HOST =
	 * "sailhero-staging.herokuapp.com"; private final static String API_HOST =
	 * "sailhero-staging.herokuapp.com";
	 */

	private final static String ACCESS_TOKEN_PATH = "oauth/token";
	private final static String API_PATH = "api";
	private final static String VERSION = "v1";
	private final static String I18N = "en";

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private Button mAuthenticateUserButton;
	private Button mRegisterUserButton;

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
			onUserAuthenticated();
		}

		mAuthenticateUserButton = (Button) findViewById(R.id.ActivityLoginAuthenticateUserButton);
		mAuthenticateUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailText = (EditText) findViewById(R.id.ActivityLoginEmailEditText);
				EditText passwordText = (EditText) findViewById(R.id.ActivityLoginPasswordEditText);
				AuthenticateUserRequest authenticateUserRequest = new AuthenticateUserRequest(
						emailText.getText().toString().trim(), passwordText.getText().toString());
				RequestAsyncTask task = new RequestAsyncTask(authenticateUserRequest,
						new AuthenticateUserResponseCreator(), LoginActivity.this,
						userAuthenticationListener);
				task.execute();
			}
		});

		mRegisterUserButton = (Button) findViewById(R.id.ActivityLoginRegisterUserButton);
		mRegisterUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent registerUserIntent = new Intent(LoginActivity.this,
						RegisterUserActivity.class);
				startActivity(registerUserIntent);
			}
		});
	}

	@Override
	public void onResume() {
		if (mSettings.getAccessToken() != null) {
			onUserAuthenticated();
		}

		super.onResume();
	}

	private RequestAsyncTask.AsyncRequestListener userAuthenticationListener = new RequestAsyncTask.AsyncRequestListener() {
		@Override
		public void onSuccess(ProcessedResponse processedResponse) {
			AuthenticateUserResponse authenticateUserResponse = (AuthenticateUserResponse) processedResponse;
			mSettings.setAccessToken(authenticateUserResponse.getAccessToken());
			mSettings.save();
			Log.d(TAG, "access token: " + mSettings.getAccessToken());

			onUserAuthenticated();
		}

		@Override
		public void onInvalidResourceOwnerException() {
			Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
		}
		
	};

	private void onUserAuthenticated() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}
}
