package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.GetRegionsRequest;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.UserProfileRequest;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends Activity implements
		GetUserProfileAsyncTask.GetUserProfileListener {

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
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	protected void onResume() {
		GetUserProfileAsyncTask getUserProfileTask = new GetUserProfileAsyncTask(
				new UserProfileRequest(), this, this);
		getUserProfileTask.execute();

		GetRegionsAsyncTask getRegionsTask = new GetRegionsAsyncTask(new GetRegionsRequest(), this,
				new GetRegionsAsyncTask.GetRegionsListener() {
					@Override
					public void onRegionsReceived() {
						Log.d(TAG, "Regions received (" + mSettings.getRegionsList().size() + ")");
					}
				});
		getRegionsTask.execute();

		super.onResume();
	}

	@Override
	public void onUserProfileReceived() {
		if (mSettings.getRegion() == null) {

		} else {
			Toast.makeText(MainActivity.this,
					"Using region: " + mSettings.getRegion().getFullName(), Toast.LENGTH_SHORT)
					.show();
		}
	}

}
