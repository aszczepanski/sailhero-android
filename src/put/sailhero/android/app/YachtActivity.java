package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.CreateYachtRequest;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class YachtActivity extends Activity {

	private EditText mNameEditText;
	private EditText mLengthEditText;
	private EditText mWidthEditText;
	private EditText mCrewEditText;

	private Button mSendButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_yacht);

		mNameEditText = (EditText) findViewById(R.id.ActivityYachtNameEditText);
		mLengthEditText = (EditText) findViewById(R.id.ActivityYachtLengthEditText);
		mWidthEditText = (EditText) findViewById(R.id.ActivityYachtWidthEditText);
		mCrewEditText = (EditText) findViewById(R.id.ActivityYachtCrewEditText);

		mNameEditText.setText("Tango");
		mLengthEditText.setText("780");
		mWidthEditText.setText("350");
		mCrewEditText.setText("7");

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
					CreateYachtRequest request = new CreateYachtRequest(name, length, width, crew);
					CreateYachtAsyncTask task = new CreateYachtAsyncTask(request,
							YachtActivity.this, new CreateYachtAsyncTask.CreateYachtListener() {
								@Override
								public void onYachtCreated() {
									Toast.makeText(YachtActivity.this, "Yacht has been saved", Toast.LENGTH_SHORT).show();
									finish();
								}

								@Override
								public void onUnprocessableEntityException(
										YachtParametersErrorsHolder errorsHolder) {
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
