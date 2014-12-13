package put.sailhero.ui;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.User;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.UpdateUserRequestHelper;
import put.sailhero.util.PrefUtils;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

public class UserActivity extends BaseActivity {

	private static int REQUEST_CODE_PICK_IMAGE = 1;

	private ActionBar mActionBar;

	private EditText mEmailEditText;
	private EditText mPasswordEditText;
	private EditText mPasswordConfirmationEditText;
	private EditText mNameEditText;
	private EditText mSurnameEditText;

	private Button mAvatarButton;
	private Button mSendButton;

	private ImageView mProfileImageView;

	private User mUser;

	// TODO: make it thread safe
	private String mEncodedAvatar;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);

		mUser = PrefUtils.getUser(UserActivity.this);

		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);

		mEmailEditText = (EditText) findViewById(R.id.email_edit_text);
		mPasswordEditText = (EditText) findViewById(R.id.password_edit_text);
		mPasswordConfirmationEditText = (EditText) findViewById(R.id.password_confirmation_edit_text);
		mNameEditText = (EditText) findViewById(R.id.name_edit_text);
		mSurnameEditText = (EditText) findViewById(R.id.surname_edit_text);

		mEmailEditText.setText(mUser.getEmail());
		mNameEditText.setText(mUser.getName());
		mSurnameEditText.setText(mUser.getSurname());

		mProfileImageView = (ImageView) findViewById(R.id.profile_image);
		if (mUser.getAvatarUrl() != null) {
			Glide.with(UserActivity.this).load(mUser.getAvatarUrl()).asBitmap().into(mProfileImageView);
		}

		mAvatarButton = (Button) findViewById(R.id.avatar_button);
		mAvatarButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE_PICK_IMAGE);
			}
		});

		mSendButton = (Button) findViewById(R.id.send_button);
		mSendButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				User user = PrefUtils.getUser(UserActivity.this);
				UpdateUserRequestHelper requestHelper = new UpdateUserRequestHelper(UserActivity.this, user.getId(),
						mEmailEditText.getText().toString().trim(), mPasswordEditText.getText().toString(),
						mPasswordConfirmationEditText.getText().toString(), mNameEditText.getText().toString().trim(),
						mSurnameEditText.getText().toString().trim(), mEncodedAvatar);
				RequestHelperAsyncTask updateTask = new RequestHelperAsyncTask(UserActivity.this, requestHelper,
						new RequestHelperAsyncTask.AsyncRequestListener() {
							@Override
							public void onSuccess(RequestHelper requestHelper) {
								Log.d(TAG, "updated user with id " + PrefUtils.getUser(UserActivity.this).getId());

								Toast.makeText(UserActivity.this, "User has been saved", Toast.LENGTH_SHORT).show();
								finish();
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
				updateTask.execute();
			}
		});

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == REQUEST_CODE_PICK_IMAGE) {
				Uri selectedImageUri = data.getData();
				Log.d(Config.TAG, selectedImageUri.toString());
				Log.d(Config.TAG, getPath(selectedImageUri));

				Glide.with(UserActivity.this)
						.load(selectedImageUri)
						.asBitmap()
						.toBytes()
						.centerCrop()
						.into(new SimpleTarget<byte[]>(256, 256) {
							@Override
							public void onResourceReady(byte[] data, GlideAnimation<? super byte[]> anim) {
								String encodedImage = Base64.encodeToString(data, Base64.NO_WRAP);
								Toast.makeText(UserActivity.this, "Bitmap loading finished.", Toast.LENGTH_SHORT)
										.show();

								Log.i(Config.TAG, encodedImage);
								Log.i(Config.TAG, "length: " + encodedImage.length());
								Log.i(Config.TAG,
										"" + Integer.valueOf((encodedImage.charAt(encodedImage.length() - 3))));
								Log.i(Config.TAG, "" + Integer.valueOf(encodedImage.charAt(encodedImage.length() - 2)));
								Log.i(Config.TAG, "" + Integer.valueOf(encodedImage.charAt(encodedImage.length() - 1)));

								mEncodedAvatar = encodedImage;
							}

							@Override
							public void onLoadFailed(Exception e, Drawable errorDrawable) {
								super.onLoadFailed(e, errorDrawable);
								Toast.makeText(UserActivity.this, "Cannot load bitmap.", Toast.LENGTH_SHORT).show();
								Log.e(Config.TAG, "Bitmap loading failed.");

								mEncodedAvatar = null;
								Glide.with(UserActivity.this)
										.load(R.drawable.person_image_empty)
										.asBitmap()
										.into(mProfileImageView);
							}
						});

				Glide.with(UserActivity.this).load(selectedImageUri).asBitmap().into(mProfileImageView);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private String getPath(Uri uri) {
		// just some safety built in 
		if (uri == null) {
			// TODO perform some logging or show user feedback
			return null;
		}
		// try to retrieve the image from the media store first
		// this will only work for images selected from gallery
		String[] projection = {
			MediaStore.Images.Media.DATA
		};
		Cursor cursor = managedQuery(uri, projection, null, null, null);
		if (cursor != null) {
			int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		}
		// this is our fallback here
		return uri.getPath();
	}

}
