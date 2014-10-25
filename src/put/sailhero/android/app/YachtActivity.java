package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.util.CreateYachtRequest;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.UpdateYachtRequest;
import put.sailhero.android.util.YachtRequest;
import put.sailhero.android.util.YachtResponse;
import put.sailhero.android.util.YachtResponseCreator;
import put.sailhero.android.util.model.Yacht;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class YachtActivity extends Activity {

	public final static String TAG = "sailhero";

	private EditText mNameEditText;
	private EditText mLengthEditText;
	private EditText mWidthEditText;
	private EditText mCrewEditText;

	private Button mSendButton;

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	private Yacht mCurrentYacht;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yacht);

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		mCurrentYacht = mSettings.getYacht();

		mNameEditText = (EditText) findViewById(R.id.ActivityYachtNameEditText);
		mLengthEditText = (EditText) findViewById(R.id.ActivityYachtLengthEditText);
		mWidthEditText = (EditText) findViewById(R.id.ActivityYachtWidthEditText);
		mCrewEditText = (EditText) findViewById(R.id.ActivityYachtCrewEditText);

		if (mCurrentYacht != null) {
			mNameEditText.setText(mCurrentYacht.getName());
			mLengthEditText.setText(mCurrentYacht.getLength().toString());
			mWidthEditText.setText(mCurrentYacht.getWidth().toString());
			mCrewEditText.setText(mCurrentYacht.getCrew().toString());
		} else {
			// TODO:
			mNameEditText.setText("Tango");
			mLengthEditText.setText("780");
			mWidthEditText.setText("350");
			mCrewEditText.setText("7");
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
					YachtRequest request;
					if (mCurrentYacht != null) {
						request = new UpdateYachtRequest(mCurrentYacht.getId(), name, length,
								width, crew);
					} else {
						request = new CreateYachtRequest(name, length, width, crew);
					}
					RequestAsyncTask task = new RequestAsyncTask(request,
							new YachtResponseCreator(), YachtActivity.this,
							new RequestAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(ProcessedResponse processedResponse) {
									YachtResponse yachtResponse = (YachtResponse) processedResponse;
									mSettings.setYacht(yachtResponse.getYacht());
									mSettings.save();
									Log.d(TAG, "created yacht with id "
											+ mSettings.getYacht().getId());

									Toast.makeText(YachtActivity.this, "Yacht has been saved",
											Toast.LENGTH_SHORT).show();
									finish();
								}

								// TODO: onYachtAlreadyCreated
								
								@Override
								public void onUnprocessableEntityException(String entityErrorsJson) {
									YachtParametersErrorsHolder errorsHolder = new YachtParametersErrorsHolder(
											entityErrorsJson);
									if (!errorsHolder.getNameErrors().isEmpty()) {
										mNameEditText.setError(errorsHolder.getNameErrors()
												.getFirst());
									}
									if (!errorsHolder.getLengthErrors().isEmpty()) {
										mLengthEditText.setError(errorsHolder.getLengthErrors()
												.getFirst());
									}
									if (!errorsHolder.getWidthErrors().isEmpty()) {
										mWidthEditText.setError(errorsHolder.getWidthErrors()
												.getFirst());
									}
									if (!errorsHolder.getCrewErrors().isEmpty()) {
										mCrewEditText.setError(errorsHolder.getCrewErrors()
												.getFirst());
									}
								}

							});
					task.execute();
				}
			}
		});
	}
}
