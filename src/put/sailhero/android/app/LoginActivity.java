package put.sailhero.android.app;

import put.sailhero.android.AccountUtils;
import put.sailhero.android.R;
import put.sailhero.android.util.AuthenticateUserRequest;
import put.sailhero.android.util.AuthenticateUserResponse;
import put.sailhero.android.util.AuthenticateUserResponseCreator;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.Request;
import android.accounts.Account;
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

	private Button mAuthenticateUserButton;
	private Button mRegisterUserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "You cannot use more than one account at the same time.",
					Toast.LENGTH_SHORT).show();
			finish();
		}

		overridePendingTransition(0, 0);

		mAuthenticateUserButton = (Button) findViewById(R.id.ActivityLoginAuthenticateUserButton);
		mAuthenticateUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				EditText emailText = (EditText) findViewById(R.id.ActivityLoginEmailEditText);
				EditText passwordText = (EditText) findViewById(R.id.ActivityLoginPasswordEditText);
				AuthenticateUserRequest authenticateUserRequest = new AuthenticateUserRequest(emailText.getText()
						.toString()
						.trim(), passwordText.getText().toString());
				RequestAsyncTask task = new RequestAsyncTask(authenticateUserRequest,
						new AuthenticateUserResponseCreator(), LoginActivity.this, userAuthenticationListener);
				task.execute();
			}
		});

		mRegisterUserButton = (Button) findViewById(R.id.ActivityLoginRegisterUserButton);
		mRegisterUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent registerUserIntent = new Intent(LoginActivity.this, RegisterUserActivity.class);
				startActivity(registerUserIntent);
			}
		});
	}

	@Override
	public void onResume() {
		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "You cannot use more than one account at the same time.",
					Toast.LENGTH_SHORT).show();
			finish();
		}

		super.onResume();
	}

	private RequestAsyncTask.AsyncRequestListener userAuthenticationListener = new RequestAsyncTask.AsyncRequestListener() {
		@Override
		public void onSuccess(ProcessedResponse processedResponse, Request request) {
			AuthenticateUserResponse authenticateUserResponse = (AuthenticateUserResponse) processedResponse;
			AuthenticateUserRequest authenticateUserRequest = (AuthenticateUserRequest) request;

			AccountUtils.addAccount(getApplicationContext(), authenticateUserRequest.username,
					authenticateUserResponse.getAccessToken(), authenticateUserResponse.getRefreshToken());

			Log.d(TAG, "access token: " + authenticateUserResponse.getAccessToken());

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
