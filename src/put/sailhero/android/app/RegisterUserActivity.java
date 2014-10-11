package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.AuthenticateUserRequest;
import put.sailhero.android.utils.CreateUserRequest;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterUserActivity extends Activity implements
		RegisterUserAsyncTask.RegisterUserListener,
		AuthenticateUserAsyncTask.AuthenticateUserListener {

	private SailHeroService mService;
	private SailHeroSettings mSettings;

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

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		mEmailEditText = (EditText) findViewById(R.id.ActivityRegisterUserEmailEditText);
		mPasswordEditText = (EditText) findViewById(R.id.ActivityRegisterUserPasswordEditText);
		mPasswordConfirmationEditText = (EditText) findViewById(R.id.ActivityRegisterUserPasswordConfirmationEditText);
		mNameEditText = (EditText) findViewById(R.id.ActivityRegisterUserNameEditText);
		mSurnameEditText = (EditText) findViewById(R.id.ActivityRegisterUserSurnameEditText);

		mRegisterUserButton = (Button) findViewById(R.id.ActivityRegisterUserRegisterUserButton);
		mRegisterUserButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CreateUserRequest createUserRequest = new CreateUserRequest(mEmailEditText
						.getText().toString().trim(), mPasswordEditText.getText().toString(),
						mPasswordConfirmationEditText.getText().toString(), mNameEditText.getText()
								.toString().trim(), mSurnameEditText.getText().toString().trim());
				RegisterUserAsyncTask task = new RegisterUserAsyncTask(createUserRequest,
						RegisterUserActivity.this, RegisterUserActivity.this);
				task.execute();
			}
		});

		mCancelButton = (Button) findViewById(R.id.ActivityRegisterUserCancelButton);
		mCancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});
	}

	@Override
	public void onUserRegistered() {
		AuthenticateUserRequest request = new AuthenticateUserRequest(mEmailEditText.getText()
				.toString().trim(), mPasswordEditText.getText().toString());
		AuthenticateUserAsyncTask task = new AuthenticateUserAsyncTask(request, this, this);
		task.execute();
	}

	@Override
	public void onUnprocessableEntityException(RegisterUserEntityErrorsHolder errorsHolder) {

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

	@Override
	public void onUserAuthenticated() {
		finish();
	}
}
