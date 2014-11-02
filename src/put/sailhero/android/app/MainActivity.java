package put.sailhero.android.app;

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
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity {

	public final static String TAG = "sailhero";

	private SailHeroService mService;
	private SailHeroSettings mSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		mService = SailHeroService.getInstance();
		mSettings = mService.getSettings();
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
