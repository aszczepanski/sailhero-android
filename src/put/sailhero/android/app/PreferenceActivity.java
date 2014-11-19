package put.sailhero.android.app;

import java.util.AbstractList;

import put.sailhero.android.R;
import put.sailhero.android.util.ProcessedResponse;
import put.sailhero.android.util.Request;
import put.sailhero.android.util.SailHeroService;
import put.sailhero.android.util.SailHeroSettings;
import put.sailhero.android.util.SelectRegionRequest;
import put.sailhero.android.util.SelectRegionResponse;
import put.sailhero.android.util.SelectRegionResponseCreator;
import put.sailhero.android.util.model.Region;
import android.content.Context;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
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

	public static class PrefFragment extends PreferenceFragment {

		private SailHeroService mService;
		private SailHeroSettings mSettings;

		private Context mContext;

		private ListPreference regionListPreference;

		public PrefFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mService = SailHeroService.getInstance();
			mSettings = mService.getSettings();

			mContext = getActivity().getApplicationContext();

			addPreferencesFromResource(R.xml.preferences);

			final AbstractList<Region> regions = mSettings.getRegionsList();
			assert (regions != null);
			final Region userRegion = mSettings.getRegion();
			CharSequence[] regionsEntries = new CharSequence[regions.size()];
			CharSequence[] regionsEntryValues = new CharSequence[regions.size()];
			for (int i = 0; i < regions.size(); i++) {
				regionsEntries[i] = regions.get(i).getFullName();
				regionsEntryValues[i] = regions.get(i).getId().toString();
			}

			regionListPreference = (ListPreference) findPreference("region_list_preference");
			regionListPreference.setEntries(regionsEntries);
			regionListPreference.setEntryValues(regionsEntryValues);
			if (userRegion == null) {
				regionListPreference.setValue(null);
			} else {
				regionListPreference.setValue(userRegion.getId().toString());
			}
			regionListPreference.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
				@Override
				public boolean onPreferenceChange(Preference preference, Object newValue) {
					final String newEntryValue = (String) newValue;
					final int newValueIndex = regionListPreference.findIndexOfValue(newEntryValue);
					Toast.makeText(getActivity(), "Preference changed to " + newEntryValue, Toast.LENGTH_SHORT).show();
					SelectRegionRequest selectRegionRequest = new SelectRegionRequest(mContext,
							Integer.valueOf(newEntryValue));
					RequestAsyncTask task = new RequestAsyncTask(selectRegionRequest,
							new SelectRegionResponseCreator(), getActivity(),
							new RequestAsyncTask.AsyncRequestListener() {
								@Override
								public void onSuccess(ProcessedResponse processedResponse, Request request) {
									SelectRegionResponse selectRegionResponse = (SelectRegionResponse) processedResponse;
									mSettings.setRegion(selectRegionResponse.getRegion());
									regionListPreference.setValueIndex(newValueIndex);
								}
							});
					task.execute();
					return false;
				}
			});
		}
	}
}
