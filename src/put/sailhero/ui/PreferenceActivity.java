package put.sailhero.ui;

import put.sailhero.Config;
import put.sailhero.R;
import put.sailhero.model.Region;
import put.sailhero.provider.SailHeroContract;
import put.sailhero.sync.LogOutRequestHelper;
import put.sailhero.sync.RequestHelper;
import put.sailhero.sync.RequestHelperAsyncTask;
import put.sailhero.sync.SelectRegionRequestHelper;
import put.sailhero.util.AccountUtils;
import put.sailhero.util.PrefUtils;
import put.sailhero.util.SyncUtils;
import android.app.Activity;
import android.app.LoaderManager;
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

	@Override
	protected void onDestroy() {
		Log.i(Config.TAG, "PreferenceActivity::onDestroy");
		super.onDestroy();
	}

	public static class PrefFragment extends PreferenceFragment implements LoaderManager.LoaderCallbacks<Cursor> {

		private Context mContext;

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
					RequestHelperAsyncTask selectRegionTask = new RequestHelperAsyncTask(mContext, "Region selection",
							"Selecting a region...", selectRegionRequestHelper,
							new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									mRegionListPreference.setValueIndex(newValueIndex);

									PrefUtils.setClosestAlert(mContext, null);
									PrefUtils.setClosestAlertToRespond(mContext, null);

									SyncUtils.syncAlerts(mContext);
									SyncUtils.syncPorts(mContext);
									SyncUtils.syncRoutes(mContext);
								}

								@Override
								public void onNotFoundException(RequestHelper requestHelper) {
									mRegionListPreference.setValueIndex(PrefUtils.getRegion(mContext).getId());
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
					RequestHelperAsyncTask logoutTask = new RequestHelperAsyncTask(mContext, "", "Logging out...",
							new LogOutRequestHelper(mContext), new RequestHelperAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(RequestHelper requestHelper) {
									Log.i(Config.TAG, "token succesfully revoked");

									logoutUser();
								}

								@Override
								public void onSystemException(RequestHelper requestHelper) {
									Log.w(Config.TAG, "token not revoked");

									logoutUser();
								}
							});
					logoutTask.execute();
					return true;
				}
			});

			getLoaderManager().initLoader(RegionQuery._TOKEN, null, this);
		}

		private void logoutUser() {
			PrefUtils.clear(getActivity());

			AccountUtils.removeActiveAccount(getActivity());

			getActivity().getContentResolver().delete(SailHeroContract.Friendship.CONTENT_URI, null, null);
			getActivity().getContentResolver().delete(SailHeroContract.Alert.CONTENT_URI, null, null);
			getActivity().getContentResolver().delete(SailHeroContract.Port.CONTENT_URI, null, null);
			getActivity().getContentResolver().delete(SailHeroContract.Route.CONTENT_URI, null, null);

			getActivity().finish();
		}

		@Override
		public void onResume() {
			super.onResume();
		}

		@Override
		public void onPause() {
			super.onPause();
		}

		@Override
		public void onAttach(Activity activity) {
			super.onAttach(activity);
		}

		@Override
		public void onDetach() {
			super.onDetach();
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

		@Override
		public Loader<Cursor> onCreateLoader(int id, Bundle args) {
			Loader<Cursor> loader = null;
			loader = new CursorLoader(mContext, SailHeroContract.Region.CONTENT_URI, RegionQuery.PROJECTION, null,
					null, SailHeroContract.Region.COLUMN_NAME_ID);
			return loader;
		}

		@Override
		public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
			if (getActivity() == null || !isAdded()) {
				return;
			}

			if (mRegionListPreference != null) {
				Region currentRegion = PrefUtils.getRegion(mContext);
				boolean isCurrentRegionOnList = false;

				CharSequence[] regionsEntries = new CharSequence[data.getCount()];
				CharSequence[] regionsEntryValues = new CharSequence[data.getCount()];

				if (data != null && data.getCount() > 0) {
					data.moveToPosition(-1);

					while (data.moveToNext()) {
						regionsEntries[data.getPosition()] = data.getString(RegionQuery.REGION_FULL_NAME);
						regionsEntryValues[data.getPosition()] = String.valueOf(data.getString(RegionQuery.REGION_ID));

						if (currentRegion != null
								&& regionsEntryValues[data.getPosition()].equals(currentRegion.getId().toString())) {
							isCurrentRegionOnList = true;
						}
					}
				}

				// assumption - regions cannot be deleted
				//	if (currentRegion != null && !isCurrentRegionOnList) {
				//		SyncUtils.syncRegions(mContext);
				//		mRegionListPreference.setEnabled(false);
				//		return;
				//	}

				mRegionListPreference.setEntries(regionsEntries);
				mRegionListPreference.setEntryValues(regionsEntryValues);

				// TODO:
				if (!isCurrentRegionOnList) {
					mRegionListPreference.setValue(null);
				} else {
					Log.d(TAG, "current region " + currentRegion.getId());
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
