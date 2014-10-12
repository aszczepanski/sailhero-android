package put.sailhero.android.app;

import put.sailhero.android.R;
import put.sailhero.android.utils.GetRegionsRequest;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.UserProfileRequest;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
	protected void onResume() {
		GetUserProfileAsyncTask task = new GetUserProfileAsyncTask(new UserProfileRequest(), this,
				this);
		task.execute();

		super.onResume();
	}

	@Override
	public void onUserProfileReceived() {
		if (mSettings.getRegion() == null) {
			GetRegionsAsyncTask task = new GetRegionsAsyncTask(new GetRegionsRequest(), this,
					new GetRegionsAsyncTask.GetRegionsListener() {
						@Override
						public void onRegionsReceived() {
							Log.d(TAG, "Regions received (" + mSettings.getRegionsList().size() + ")");
						}
					});
			task.execute();
		} else {
			Toast.makeText(MainActivity.this,
					"Using region: " + mSettings.getRegion().getFullName(), Toast.LENGTH_SHORT)
					.show();
		}
	}

}
