package put.sailhero.android.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SailHeroSharedPreferencesSettings extends SailHeroSettings {

	private Context context;

	public SailHeroSharedPreferencesSettings(Context context) {
		this.context = context;

		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
		accessToken = sharedPref.getString("accessToken", null);
	}

	@Override
	public void save() {
		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("accessToken", accessToken);
		editor.commit();
	}

	@Override
	public void clear() {
		accessToken = null;		
	}

}
