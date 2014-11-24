package put.sailhero.android;

import put.sailhero.account.AccountUtils;
import put.sailhero.model.Region;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RetrieveUserRequestHelper;
import put.sailhero.ui.BaseActivity;
import put.sailhero.ui.RequestHelperAsyncTask;
import put.sailhero.util.PrefUtils;
import android.accounts.Account;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class DashboardActivity extends BaseActivity {

	Context mContext;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dashboard);

		mContext = DashboardActivity.this;

		overridePendingTransition(0, 0);

		Account account = AccountUtils.getActiveAccount(getApplicationContext());
		if (account != null) {
			Toast.makeText(getApplicationContext(), "Using: " + account.name, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	protected int getSelfNavDrawerItem() {
		return NAVDRAWER_ITEM_DASHBOARD;
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
			Toast.makeText(DashboardActivity.this, "Using region: " + selectedRegion.getFullName(), Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d(TAG, "MainActivity::onSaveInstanceState");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.dashboard, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
