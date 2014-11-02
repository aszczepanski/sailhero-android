package put.sailhero.android.app;

import java.io.IOException;

import put.sailhero.android.R;
import put.sailhero.android.util.GetPortsRequest;
import put.sailhero.android.util.GetPortsResponse;
import put.sailhero.android.util.GetPortsResponseCreator;
import put.sailhero.android.util.GetRegionsRequest;
import put.sailhero.android.util.GetRegionsResponse;
import put.sailhero.android.util.GetRegionsResponseCreator;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.UnauthorizeUserRequest;
import put.sailhero.android.util.UnauthorizeUserResponseCreator;
import put.sailhero.android.util.UserProfileRequest;
import put.sailhero.android.util.UserProfileResponse;
import put.sailhero.android.util.UserProfileResponseCreator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;

public class MainActivity extends Activity {

	public final static String TAG = "sailhero";

	public static final String PROPERTY_REG_ID = "registration_id";
	String SENDER_ID = "804800551458";
	GoogleCloudMessaging gcm;
	String regId;

	Context context;

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();

		context = getApplicationContext();

		regId = getRegistrationId(context);
		if (regId.isEmpty()) {
			registerInBackground();
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regId;

					sendRegistrationIdToBackend();

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error: " + ex.getMessage();
					// If there is an error, don't just keep trying to register.
					// Require the user to click a button again, or perform
					// exponential back-off.
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
				super.onPostExecute(msg);
			}

		}.execute(null, null, null);
		// ...
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGCMPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
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
		RequestAsyncTask getUserProfileTask = new RequestAsyncTask(new UserProfileRequest(),
				new UserProfileResponseCreator(), this,
				new RequestAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(ProcessedResponse processedResponse) {
						UserProfileResponse userProfileResponse = (UserProfileResponse) processedResponse;

						mSettings.setUser(userProfileResponse.getUser());
						mSettings.setYacht(userProfileResponse.getYacht());
						mSettings.setRegion(userProfileResponse.getRegion());

						onUserProfileReceived();
					}
				});
		getUserProfileTask.execute();

		RequestAsyncTask getRegionsTask = new RequestAsyncTask(new GetRegionsRequest(),
				new GetRegionsResponseCreator(), this, new RequestAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(ProcessedResponse processedResponse) {
						GetRegionsResponse getRegionsResponse = (GetRegionsResponse) processedResponse;

						mSettings.setRegionsList(getRegionsResponse.getRegions());
						Log.d(TAG, "Regions received (" + mSettings.getRegionsList().size() + ")");
					}
				});
		getRegionsTask.execute();

		RequestAsyncTask getPortsTask = new RequestAsyncTask(new GetPortsRequest(),
				new GetPortsResponseCreator(), this, new RequestAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(ProcessedResponse processedResponse) {
						GetPortsResponse getPortsResponse = (GetPortsResponse) processedResponse;

						mSettings.setPorts(getPortsResponse.getPorts());
						Log.d(TAG, "Ports received (" + mSettings.getPorts().size() + ")");
					}
				});
		getPortsTask.execute();

		super.onResume();
	}

	public void onUserProfileReceived() {
		if (mSettings.getRegion() == null) {

		} else {
			Toast.makeText(MainActivity.this,
					"Using region: " + mSettings.getRegion().getFullName(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	public void logout() {
		RequestAsyncTask logoutTask = new RequestAsyncTask(new UnauthorizeUserRequest(),
				new UnauthorizeUserResponseCreator(), this,
				new RequestAsyncTask.AsyncRequestListener() {
					@Override
					public void onSuccess(ProcessedResponse processedResponse) {
						mSettings.clear();
						mSettings.save();
						Log.d(TAG, "Token revoked");
						Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
						startActivity(loginIntent);
						finish();
					}
				});
		logoutTask.execute();
	}
}
