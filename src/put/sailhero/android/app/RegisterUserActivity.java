package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.AuthenticateUserRequest;
import put.sailhero.android.utils.AuthenticateUserResponseCreator;
import put.sailhero.android.utils.CreateUserRequest;
import put.sailhero.android.utils.CreateUserResponse;
import put.sailhero.android.utils.CreateUserResponseCreator;
import put.sailhero.android.utils.ProcessedResponse;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class RegisterUserActivity extends Activity {

	public final static String TAG = "sailhero";

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
				RequestAsyncTask task = new RequestAsyncTask(createUserRequest,
						new CreateUserResponseCreator(), RegisterUserActivity.this,
						registerUserListener);
				task.execute();
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

	private RequestAsyncTask.AsyncRequestListener registerUserListener = new RequestAsyncTask.AsyncRequestListener() {
		@Override
		public void onSuccess(ProcessedResponse processedResponse) {
			CreateUserResponse createUserResponse = (CreateUserResponse) processedResponse;
			Log.d(TAG, "Created user with id: " + createUserResponse.getUser().getId());

			AuthenticateUserRequest request = new AuthenticateUserRequest(mEmailEditText.getText()
					.toString().trim(), mPasswordEditText.getText().toString());

			RequestAsyncTask task = new RequestAsyncTask(request,
					new AuthenticateUserResponseCreator(), RegisterUserActivity.this,
					new RequestAsyncTask.AsyncRequestListener() {
						@Override
						public void onSuccess(ProcessedResponse processedResponse) {
							finish();
						}
					});
			task.execute();
		}

		@Override
		public void onUnprocessableEntityException(String entityErrorsJson) {
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

	};
}
