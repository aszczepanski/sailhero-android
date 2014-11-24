package put.sailhero.ui;

import java.io.IOException;

import put.sailhero.account.AccountUtils;
import put.sailhero.android.R;
import put.sailhero.model.Region;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.LogOutRequestHelper;
import put.sailhero.sync.RegisterGcmRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RetrieveUserRequestHelper;
import put.sailhero.util.PrefUtils;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends BaseActivity {

	public final static String TAG = "sailhero";

	public static final String PROPERTY_REG_ID = "registration_id";
	String SENDER_ID = "804800551458";
	GoogleCloudMessaging gcm;
	String regId;

	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		Log.w(TAG, "BRAND: " + Build.BRAND);
		Log.w(TAG, "MODEL: " + Build.MODEL);

		mContext = MainActivity.this;
		/*
				regId = getRegistrationId(context);
				if (regId.isEmpty()) {
					registerInBackground();
				}
		*/
		getSupportActionBar().setTitle("SailHero");

		overridePendingTransition(0, 0);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "Using: " + account.name, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_MAIN;
	}

	@Override
	public void onBackPressed() {
		new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Closing Activity")
				.setMessage("Are you sure you want to exit SailHero?")
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						finish();
					}

				})
				.setNegativeButton("No", null)
				.show();
	}

	@Override
	protected void onStart() {
		Log.d(TAG, "MainActivity::onStart");
		super.onStart();
	}

	@Override
	protected void onPause() {
		Log.d(TAG, "MainActivity::onPause");
		super.onPause();
	}

	@Override
	protected void onStop() {
		Log.d(TAG, "MainActivity::onStop");
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		Log.d(TAG, "MainActivity::onDestroy");
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d(TAG, "MainActivity::onSaveInstanceState");
		super.onSaveInstanceState(outState);
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {

			private Exception exception;

			@Override
			protected String doInBackground(Void... params) {
				// String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(mContext);
					}
					regId = gcm.register(SENDER_ID);
					// msg = "Device registered, registration ID=" + regId;

					// sendRegistrationIdToBackend(regId);

					storeRegistrationId(mContext, regId);
				} catch (IOException ex) {
					exception = ex;
				}
				return regId;
			}

			@Override
			protected void onPostExecute(String registrationId) {
				if (exception == null) {
					sendRegistrationIdToBackend(registrationId);
				} else {
					Toast.makeText(mContext, "Error: " + exception.getMessage(), Toast.LENGTH_LONG).show();
				}

				super.onPostExecute(registrationId);
			}

		}.execute(null, null, null);
	}

	private void sendRegistrationIdToBackend(String registrationId) {
		RequestHelperAsyncTask registerGcmTask = new RequestHelperAsyncTask(mContext, new RegisterGcmRequestHelper(
				mContext, registrationId), new RequestHelperAsyncTask.AsyncRequestListener() {
			@Override
			public void onSuccess(RequestHelper requestHelper) {
				Log.d(TAG, "Gcm registered on SailHero server.");
			}
		});
		registerGcmTask.execute();
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		} else {
			Log.i(TAG, "Registration id: " + registrationId);
		}

		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.

		// int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
		// Integer.MIN_VALUE);
		// int currentVersion = getAppVersion(context);
		// if (registeredVersion != currentVersion) {
		// Log.i(TAG, "App version changed.");
		// return "";
		// }

		return registrationId;
	}

	private SharedPreferences getGCMPreferences(Context context) {
		return getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGCMPreferences(context);
		// int appVersion = getAppVersion(context);
		// Log.i(TAG, "Saving regId on app version " + appVersion);
		Log.i(TAG, "Saving regId");
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		// editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent intent = null;
		switch (item.getItemId()) {
		case R.id.action_settings:
			intent = new Intent(MainActivity.this, PreferenceActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_alert:
			intent = new Intent(MainActivity.this, AlertActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_alert_map:
			intent = new Intent(MainActivity.this, AlertMapActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_test_map:
			intent = new Intent(MainActivity.this, TestMapActivity.class);
			startActivity(intent);
			return true;
		case R.id.action_logout:
			logout();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (isFinishing()) {
			return;
		}

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		ContentResolver.setIsSyncable(account, SailHeroContract.CONTENT_AUTHORITY, 1);

		Bundle bundle = new Bundle();
		// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
		bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);

		ContentResolver.requestSync(account, SailHeroContract.CONTENT_AUTHORITY, bundle);

		RequestHelperAsyncTask getUserProfileTask = new RequestHelperAsyncTask(mContext, new RetrieveUserRequestHelper(
				mContext), new RequestHelperAsyncTask.AsyncRequestListener() {

			@Override
			public void onSuccess(RequestHelper requestHelper) {
				onUserProfileReceived();
			}
		});
		getUserProfileTask.execute();

	}

	public void onUserProfileReceived() {
		Region selectedRegion = PrefUtils.getRegion(mContext);
		if (selectedRegion == null) {

		} else {
			Toast.makeText(MainActivity.this, "Using region: " + selectedRegion.getFullName(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	// TODO: move to BaseActivity
	public void logout() {
		RequestHelperAsyncTask logoutTask = new RequestHelperAsyncTask(mContext, new LogOutRequestHelper(mContext),
				new RequestHelperAsyncTask.AsyncRequestListener() {

					@Override
					public void onSuccess(RequestHelper requestHelper) {
						// TODO: clear db

						AccountUtils.removeActiveAccount(getApplicationContext());

						Log.d(TAG, "Token revoked");
						Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(loginIntent);
						finish();
					}
				});
		logoutTask.execute();
	}
}
