package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.LogInRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.SyncUtils;
import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
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

	private Context mContext;

	private EditText mEmailEditText;
	private EditText mPasswordEditText;

	private Button mAuthenticateUserButton;
	private Button mRegisterUserButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		Log.e(TAG, "LoginActivity::onCreate");

		mContext = LoginActivity.this;

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "You cannot use more than one account at the same time.",
					Toast.LENGTH_SHORT).show();
			finish();
		}

		overridePendingTransition(0, 0);

		mEmailEditText = (EditText) findViewById(R.id.ActivityLoginEmailEditText);
		mPasswordEditText = (EditText) findViewById(R.id.ActivityLoginPasswordEditText);

		mAuthenticateUserButton = (Button) findViewById(R.id.ActivityLoginAuthenticateUserButton);
		mAuthenticateUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final LogInRequestHelper logInRequestHelper = new LogInRequestHelper(mContext, mEmailEditText.getText()
						.toString()
						.trim(), mPasswordEditText.getText().toString());
				RequestHelperAsyncTask logInTask = new RequestHelperAsyncTask(mContext, "Authenticating",
						"Logging in...", logInRequestHelper, new RequestHelperAsyncTask.AsyncRequestListener() {
							@Override
							public void onSuccess(RequestHelper requestHelper) {
								AccountUtils.addAccount(getApplicationContext(), logInRequestHelper.getSentUsername(),
										logInRequestHelper.getRetrievedAccessToken(),
										logInRequestHelper.getRetrievedRefreshToken());

								Log.d(TAG, "access token: " + logInRequestHelper.getRetrievedAccessToken());

								onUserAuthenticated();
							}

							@Override
							public void onInvalidResourceOwnerException(RequestHelper requestHelper) {
								Toast.makeText(mContext, logInRequestHelper.mErrorMessage, Toast.LENGTH_LONG).show();
							}
						});

				logInTask.execute();
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
	protected void onDestroy() {
		super.onDestroy();
		Log.e(TAG, "LoginActivity::onDestroy");
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

	private void onUserAuthenticated() {
		mContext.getContentResolver().delete(SailHeroContract.Friendship.CONTENT_URI, null, null);
		mContext.getContentResolver().delete(SailHeroContract.Alert.CONTENT_URI, null, null);
		mContext.getContentResolver().delete(SailHeroContract.Port.CONTENT_URI, null, null);
		mContext.getContentResolver().delete(SailHeroContract.Route.CONTENT_URI, null, null);

		PrefUtils.clear(mContext);

		SyncUtils.syncAll(mContext);

		Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
		startActivity(intent);
		finish();
	}
}
