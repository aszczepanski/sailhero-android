package put.sailhero.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SailHeroSharedPreferencesSettings extends SailHeroSettings {

	private Context context;

	public SailHeroSharedPreferencesSettings(Context context) {
		this.context = context;

		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
	}

	@Override
	public void save() {
		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
	}

	@Override
	public void clear() {
		user = null;
		yacht = null;
		region = null;

		alerts = null;
		ports = null;
		regionsList = null;
	}

}
