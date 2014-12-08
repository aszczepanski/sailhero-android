package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.CreateUserRequestHelper;
import put.sailhero.sync.LogInRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.util.AccountUtils;
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

public class RegisterUserActivity extends Activity {

	public final static String TAG = "sailhero";

	private Context mContext;

	private Button mRegisterUserButton;
	private Button mCancelButton;

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordConfirmationEditText;
	private EditText mNameEditText;
	private EditText mSurnameEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_user);

		mContext = RegisterUserActivity.this;

		mEmailEditText = (EditText) findViewById(R.id.ActivityRegisterUserEmailEditText);
		mPasswordEditText = (EditText) findViewById(R.id.ActivityRegisterUserPasswordEditText);
		mPasswordConfirmationEditText = (EditText) findViewById(R.id.ActivityRegisterUserPasswordConfirmationEditText);
		mNameEditText = (EditText) findViewById(R.id.ActivityRegisterUserNameEditText);
		mSurnameEditText = (EditText) findViewById(R.id.ActivityRegisterUserSurnameEditText);

		mRegisterUserButton = (Button) findViewById(R.id.ActivityRegisterUserRegisterUserButton);
		mRegisterUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final CreateUserRequestHelper createUserRequestHelper = new CreateUserRequestHelper(mContext,
						mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString(),
						mPasswordConfirmationEditText.getText().toString(), mNameEditText.getText().toString().trim(),
						mSurnameEditText.getText().toString().trim());
				RequestHelperAsyncTask createUserTask = new RequestHelperAsyncTask(mContext, createUserRequestHelper,
						new RequestHelperAsyncTask.AsyncRequestListener() {

							@Override
							public void onSuccess(RequestHelper requestHelper) {
								final LogInRequestHelper logInRequestHelper = new LogInRequestHelper(mContext,
										createUserRequestHelper.getEmail(), createUserRequestHelper.getPassword()
												.toString());
								RequestHelperAsyncTask logInTask = new RequestHelperAsyncTask(mContext,
										logInRequestHelper, new RequestHelperAsyncTask.AsyncRequestListener() {
											@Override
											public void onSuccess(RequestHelper requestHelper) {
												AccountUtils.addAccount(getApplicationContext(),
														logInRequestHelper.getSentUsername(),
														logInRequestHelper.getRetrievedAccessToken(),
														logInRequestHelper.getRetrievedRefreshToken());

												Log.d(TAG,
														"access token: " + logInRequestHelper.getRetrievedAccessToken());

												onUserAuthenticated();
											}

											@Override
											public void onInvalidResourceOwnerException(RequestHelper requestHelper) {
												Toast.makeText(mContext, logInRequestHelper.mErrorMessage,
														Toast.LENGTH_LONG).show();
											}
										});

								logInTask.execute();
							}

							@Override
							public void onUnprocessableEntityException(RequestHelper requestHelper,
									String entityErrorsJson) {
								RegisterUserEntityErrorsHolder errorsHolder = new RegisterUserEntityErrorsHolder(
										entityErrorsJson);

								if (!errorsHolder.getEmailErrors().isEmpty()) {
									mEmailEditText.setError(errorsHolder.getEmailErrors().getFirst());
								}
								if (!errorsHolder.getPasswordErrors().isEmpty()) {
									mPasswordEditText.setError(errorsHolder.getPasswordErrors().getFirst());
								}
								if (!errorsHolder.getPasswordConfirmationErrors().isEmpty()) {
									mPasswordConfirmationEditText.setError(errorsHolder.getPasswordConfirmationErrors()
											.getFirst());
								}
								if (!errorsHolder.getNameErrors().isEmpty()) {
									mNameEditText.setError(errorsHolder.getNameErrors().getFirst());
								}
								if (!errorsHolder.getSurnameErrors().isEmpty()) {
									mSurnameEditText.setError(errorsHolder.getSurnameErrors().getFirst());
								}
							}

						});
				createUserTask.execute();
			}
		});

		mCancelButton = (Button) findViewById(R.id.ActivityRegisterUserCancelButton);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}

	private void onUserAuthenticated() {
		// TODO: add it to user registration and logout
		mContext.getContentResolver().delete(SailHeroContract.Alert.CONTENT_URI, null, null);
		mContext.getContentResolver().delete(SailHeroContract.Friendship.CONTENT_URI, null, null);

		Intent intent = new Intent(RegisterUserActivity.this, DashboardActivity.class);
		startActivity(intent);
		finish();
	}
}
