package put.sailhero.ui;

import put.sailhero.R;
import put.sailhero.model.Region;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.LogOutRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.SelectRegionRequestHelper;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.ThrottledContentObserver;
import android.accounts.Account;
import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

public class PreferenceActivity extends BaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle("Settings");

		Toolbar toolbar = getActionBarToolbar();
		toolbar.setNavigationOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction().add(R.id.main_content, new PrefFragment()).commit();
		}

		overridePendingTransition(0, 0);
	}

	public static class PrefFragment extends PreferenceFragment implements LoaderManager.LoaderCallbacks<Cursor> {

		private Context mContext;
		private Account mAccount;

		private ThrottledContentObserver mRegionsObserver;

		private ListPreference mRegionListPreference;

		private Preference mLogoutPreference;

		public PrefFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mContext = getActivity();

			addPreferencesFromResource(R.xml.preferences);

			mRegionListPreference = (ListPreference) findPreference("region_list_preference");
			mRegionListPreference.setEnabled(false);

			mRegionListPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					final String newEntryValue = (String) newValue;
					final int newValueIndex = mRegionListPreference.findIndexOfValue(newEntryValue);
					Log.i(TAG, "Preference changed to " + newEntryValue);
					Toast.makeText(getActivity(), "Preference changed to " + newEntryValue, Toast.LENGTH_SHORT).show();

					SelectRegionRequestHelper selectRegionRequestHelper = new SelectRegionRequestHelper(mContext,
							Integer.valueOf(newEntryValue));
					RequestHelperAsyncTask selectRegionTask = new RequestHelperAsyncTask(mContext,
							selectRegionRequestHelper, new RequestHelperAsyncTask.AsyncRequestListener() {

								@Override
								public void onSuccess(RequestHelper requestHelper) {
									mRegionListPreference.setValueIndex(newValueIndex);
								}
							});
					selectRegionTask.execute();

					return false;
				}
			});

			mLogoutPreference = (Preference) findPreference("logout_preference");
			mLogoutPreference.setOnPreferenceClickListener(new OnPreferenceClickListener() {
				@Override
				public boolean onPreferenceClick(Preference preference) {
					RequestHelperAsyncTask logoutTask = new RequestHelperAsyncTask(mContext, new LogOutRequestHelper(
							mContext), new RequestHelperAsyncTask.AsyncRequestListener() {
						@Override
						public void onSuccess(RequestHelper requestHelper) {
							// TODO: perform clear in background

							PrefUtils.clear(getActivity());

							AccountUtils.removeActiveAccount(getActivity());

							getActivity().getContentResolver().delete(SailHeroContract.Friendship.CONTENT_URI, null,
									null);
							getActivity().getContentResolver().delete(SailHeroContract.Alert.CONTENT_URI, null, null);

							getActivity().finish();
						}
					});
					logoutTask.execute();
					return true;
				}
			});
		}

		@Override
		public void onResume() {
			super.onResume();

			mAccount = AccountUtils.getActiveAccount(mContext);

			mRegionsObserver = new ThrottledContentObserver(new ThrottledContentObserver.Callbacks() {
				@Override
				public void onThrottledContentObserverFired() {
					onRegionsChanged();
				}
			});
			getActivity().getContentResolver().registerContentObserver(SailHeroContract.Region.CONTENT_URI, true,
					mRegionsObserver);

			Bundle bundle = new Bundle();
			// Disable sync backoff and ignore sync preferences. In other words...perform sync NOW!
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
			bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
			ContentResolver.requestSync(mAccount, SailHeroContract.CONTENT_AUTHORITY, bundle);

			onRegionsChanged();
		}

		@Override
		public void onPause() {
			super.onPause();

			getActivity().getContentResolver().unregisterContentObserver(mRegionsObserver);
		}

		private interface RegionQuery {
			int _TOKEN = 0x1;

			String[] PROJECTION = {
					SailHeroContract.Region.COLUMN_NAME_ID,
					SailHeroContract.Region.COLUMN_NAME_FULL_NAME
			};

			int REGION_ID = 0;
			int REGION_FULL_NAME = 1;
		}

		private void onRegionsChanged() {
			Toast.makeText(mContext, "onRegionsChanged()", Toast.LENGTH_SHORT).show();
			Log.d(TAG, "onRegionsChanged()");

			getLoaderManager().restartLoader(RegionQuery._TOKEN, null, PrefFragment.this);
		}

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Region.CONTENT_URI, RegionQuery.PROJECTION, null,
					null, SailHeroContract.Region.COLUMN_NAME_ID);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (getActivity() == null) {
				return;
			}

			Toast.makeText(mContext, "cursor load finished", Toast.LENGTH_SHORT).show();

			if (mRegionListPreference != null) {
				CharSequence[] regionsEntries = new CharSequence[data.getCount()];
				CharSequence[] regionsEntryValues = new CharSequence[data.getCount()];

				while (data.moveToNext()) {
					regionsEntries[data.getPosition()] = data.getString(RegionQuery.REGION_FULL_NAME);
					regionsEntryValues[data.getPosition()] = String.valueOf(data.getString(RegionQuery.REGION_ID));
				}

				mRegionListPreference.setEntries(regionsEntries);
				mRegionListPreference.setEntryValues(regionsEntryValues);

				Region currentRegion = PrefUtils.getRegion(mContext);
				// TODO:
				if (currentRegion == null) {
					mRegionListPreference.setValue(null);
				} else {
					mRegionListPreference.setValue(currentRegion.getId().toString());
				}

				if (data.getCount() > 0) {
					mRegionListPreference.setEnabled(true);
				} else {
					mRegionListPreference.setEnabled(false);
				}
			}

		}

		@Override
		public void onLoaderReset(Loader<Cursor> loader) {
		}
	}
}
