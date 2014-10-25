package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.GetPortsRequest;
import put.sailhero.android.utils.GetPortsResponse;
import put.sailhero.android.utils.GetPortsResponseCreator;
import put.sailhero.android.utils.GetRegionsRequest;
import put.sailhero.android.utils.GetRegionsResponse;
import put.sailhero.android.utils.GetRegionsResponseCreator;
import put.sailhero.android.utils.ProcessedResponse;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.UserProfileRequest;
import put.sailhero.android.utils.UserProfileResponse;
import put.sailhero.android.utils.UserProfileResponseCreator;
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
		case R.id.action_test_map:
			intent = new Intent(MainActivity.this, TestMapActivity.class);
			startActivity(intent);
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

}
