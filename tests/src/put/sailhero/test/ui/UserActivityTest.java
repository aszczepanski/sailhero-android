package put.sailhero.test.ui;

import put.sailhero.R;
import put.sailhero.ui.UserActivity;
import android.app.Instrumentation;
import android.support.v7.app.ActionBar;
import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;

public class UserActivityTest extends ActivityInstrumentationTestCase2<UserActivity> {

	private UserActivity mActivity;

	private ActionBar mActionBar;

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordConfirmationEditText;
	private EditText mNameEditText;
	private EditText mSurnameEditText;

	private Button mAvatarButton;
	private Button mSendButton;

	private ImageView mProfileImageView;

	private CheckBox mSharePositionCheckBox;

	public UserActivityTest() {
		super(UserActivity.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		setActivityInitialTouchMode(false);

		mActivity = getActivity();

		mActionBar = mActivity.getSupportActionBar();

		mEmailEditText = (EditText) mActivity.findViewById(R.id.email_edit_text);
		mPasswordEditText = (EditText) mActivity.findViewById(R.id.password_edit_text);
		mPasswordConfirmationEditText = (EditText) mActivity.findViewById(R.id.password_confirmation_edit_text);
		mNameEditText = (EditText) mActivity.findViewById(R.id.name_edit_text);
		mSurnameEditText = (EditText) mActivity.findViewById(R.id.surname_edit_text);

		mAvatarButton = (Button) mActivity.findViewById(R.id.avatar_button);
		mSendButton = (Button) mActivity.findViewById(R.id.send_button);

		mProfileImageView = (ImageView) mActivity.findViewById(R.id.profile_image);

		mSharePositionCheckBox = (CheckBox) mActivity.findViewById(R.id.share_position_check_box);
	}

	public void testPrecondition() {
		assertTrue(mActionBar != null);

		assertTrue(mEmailEditText != null);
		assertTrue(mPasswordEditText != null);
		assertTrue(mPasswordConfirmationEditText != null);
		assertTrue(mNameEditText != null);
		assertTrue(mSurnameEditText != null);

		assertTrue(mAvatarButton != null);
		assertTrue(mSendButton != null);

		assertTrue(mProfileImageView != null);

		assertTrue(mSharePositionCheckBox != null);

		assertTrue(TextUtils.isEmpty(mPasswordEditText.getText().toString()));
		assertTrue(TextUtils.isEmpty(mPasswordConfirmationEditText.getText().toString()));
	}

	public void testEmailNotEditable() throws Throwable {
		final String initialText = "xxx";

		mActivity.runOnUiThread(new Runnable() {
			public void run() {
				mEmailEditText.setText(initialText);
				mEmailEditText.requestFocus();
			}
		});

		getInstrumentation().waitForIdleSync();
		sendKeys("A B C");
		getInstrumentation().waitForIdleSync();

		assertEquals(mEmailEditText.getText().toString(), initialText);
	}

	@UiThreadTest
	public void testStatePause() {
		Instrumentation mInstr = this.getInstrumentation();

		final String text1 = "asd";
		final String text2 = "fgh";
		final String text3 = "qwe";
		final String text4 = "rty";

		mPasswordEditText.setText(text1);
		mPasswordConfirmationEditText.setText(text2);
		mNameEditText.setText(text3);
		mSurnameEditText.setText(text4);

		mInstr.callActivityOnPause(mActivity);
		mInstr.callActivityOnResume(mActivity);

		assertEquals(mPasswordEditText.getText().toString(), text1);
		assertEquals(mPasswordConfirmationEditText.getText().toString(), text2);
		assertEquals(mNameEditText.getText().toString(), text3);
		assertEquals(mSurnameEditText.getText().toString(), text4);
	}
}
