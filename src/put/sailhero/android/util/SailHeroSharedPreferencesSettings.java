package put.sailhero.android.util;

import android.content.Context;
import android.content.SharedPreferences;

public class SailHeroSharedPreferencesSettings extends SailHeroSettings {

	private Context context;

	public SailHeroSharedPreferencesSettings(Context context) {
		this.context = context;

		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
		accessToken = sharedPref.getString("accessToken", null);
		refreshToken = sharedPref.getString("refreshToken", null);
	}

	@Override
	public void save() {
		SharedPreferences sharedPref = context.getSharedPreferences("default", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPref.edit();
		editor.putString("accessToken", accessToken);
		editor.putString("refreshToken", refreshToken);
		editor.commit();
	}

	@Override
	public void clear() {
		accessToken = null;
		refreshToken = null;
		
		user = null;
		yacht = null;
		region = null;
	}

}
