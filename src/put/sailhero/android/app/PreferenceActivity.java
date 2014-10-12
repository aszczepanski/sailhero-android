package put.sailhero.android.app;

import put.sailhero.android.R;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

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

		public PrefFragment() {
		}

		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			
			addPreferencesFromResource(R.xml.preferences);
		}

	}
}
