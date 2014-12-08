package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Yacht;
import put.sailhero.sync.CreateYachtRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.UpdateYachtRequestHelper;
import put.sailhero.util.PrefUtils;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class YachtActivity extends BaseActivity {

	public final static String TAG = "sailhero";

	private ActionBar mActionBar;

	private EditText mNameEditText;
	private EditText mLengthEditText;
	private EditText mWidthEditText;
	private EditText mCrewEditText;

	private Button mSendButton;

	private Yacht mCurrentYacht;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yacht);

		mCurrentYacht = PrefUtils.getYacht(YachtActivity.this);

		mActionBar = getSupportActionBar();
		mActionBar.setTitle("Update your yacht");
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mNameEditText = (EditText) findViewById(R.id.ActivityYachtNameEditText);
		mLengthEditText = (EditText) findViewById(R.id.ActivityYachtLengthEditText);
		mWidthEditText = (EditText) findViewById(R.id.ActivityYachtWidthEditText);
		mCrewEditText = (EditText) findViewById(R.id.ActivityYachtCrewEditText);

		if (mCurrentYacht != null) {
			mNameEditText.setText(mCurrentYacht.getName());
			mLengthEditText.setText(mCurrentYacht.getLength().toString());
			mWidthEditText.setText(mCurrentYacht.getWidth().toString());
			mCrewEditText.setText(mCurrentYacht.getCrew().toString());
		}

		mSendButton = (Button) findViewById(R.id.ActivityYachtSendButton);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String name = mNameEditText.getText().toString().trim();
				Integer length = null;
				Integer width = null;
				Integer crew = null;
				try {
					length = Integer.valueOf(mLengthEditText.getText().toString());
				} catch (NumberFormatException e) {
					mLengthEditText.setError("must be an integer");
				}
				try {
					width = Integer.valueOf(mWidthEditText.getText().toString());
				} catch (NumberFormatException e) {
					mWidthEditText.setError("must be an integer");
				}
				try {
					crew = Integer.valueOf(mCrewEditText.getText().toString());
				} catch (NumberFormatException e) {
					mCrewEditText.setError("must be an integer");
				}

				if (length != null && width != null && crew != null) {
					RequestHelper requestHelper = null;
					if (mCurrentYacht != null) {
						requestHelper = new UpdateYachtRequestHelper(YachtActivity.this, mCurrentYacht.getId(), name,
								length, width, crew);
					} else {
						requestHelper = new CreateYachtRequestHelper(YachtActivity.this, name, length, width, crew);
					}
					// TODO:
					RequestHelperAsyncTask task = new RequestHelperAsyncTask(YachtActivity.this, requestHelper,
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Log.d(TAG, "created yacht with id "
											+ PrefUtils.getYacht(YachtActivity.this).getId());

									Toast.makeText(YachtActivity.this, "Yacht has been saved", Toast.LENGTH_SHORT)
											.show();
									finish();
								}

								@Override
								public void onYachtAlreadyCreatedException(RequestHelper requestHelper) {
									Log.w(TAG, "yacht already created");

									// TODO: do second request maybe
									Toast.makeText(YachtActivity.this, "Yacht has already been created.",
											Toast.LENGTH_SHORT).show();
									finish();
								}

								@Override
								public void onUnprocessableEntityException(RequestHelper requestHelper,
										String entityErrorsJson) {
									YachtParametersErrorsHolder errorsHolder = new YachtParametersErrorsHolder(
											entityErrorsJson);
									if (!errorsHolder.getNameErrors().isEmpty()) {
										mNameEditText.setError(errorsHolder.getNameErrors().getFirst());
									}
									if (!errorsHolder.getLengthErrors().isEmpty()) {
										mLengthEditText.setError(errorsHolder.getLengthErrors().getFirst());
									}
									if (!errorsHolder.getWidthErrors().isEmpty()) {
										mWidthEditText.setError(errorsHolder.getWidthErrors().getFirst());
									}
									if (!errorsHolder.getCrewErrors().isEmpty()) {
										mCrewEditText.setError(errorsHolder.getCrewErrors().getFirst());
									}
								}
							});
					task.execute();
				}
			}
		});
	}
}
