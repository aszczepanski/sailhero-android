package put.sailhero.android.app;

import java.util.AbstractList;

import put.sailhero.android.R;
import put.sailhero.android.utils.Region;
import put.sailhero.android.utils.SailHeroService;
import put.sailhero.android.utils.SailHeroSettings;
import put.sailhero.android.utils.SelectRegionRequest;
import android.app.Activity;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceFragment;
import android.widget.Toast;

public class PreferenceActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_preference);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
					.add(R.id.ActivityPreferenceContainer, new PrefFragment()).commit();
		}

	}

	public static class PrefFragment extends PreferenceFragment {

		private SailHeroService mService;
		private SailHeroSettings mSettings;

		public PrefFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			mService = SailHeroService.getInstance();
			mSettings = mService.getSettings();

			addPreferencesFromResource(R.xml.preferences);

			final AbstractList<Region> regions = mSettings.getRegionsList();
			assert(regions != null);
			final Region userRegion = mSettings.getRegion();
			CharSequence[] regionsEntries = new CharSequence[regions.size()];
			CharSequence[] regionsEntryValues = new CharSequence[regions.size()];
			for (int i = 0; i < regions.size(); i++) {
				regionsEntries[i] = regions.get(i).getFullName();
				regionsEntryValues[i] = regions.get(i).getId().toString();
			}

			final ListPreference regionListPreference = (ListPreference) findPreference("region_list_preference");
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
					Toast.makeText(getActivity(), "Preference changed to " + newEntryValue,
							Toast.LENGTH_SHORT).show();
					SelectRegionAsyncTask task = new SelectRegionAsyncTask(new SelectRegionRequest(
							Integer.valueOf(newEntryValue)), getActivity(),
							new SelectRegionAsyncTask.SelectRegionListener() {
								@Override
								public void onRegionSelected() {
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
