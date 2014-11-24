package put.sailhero.util;

import put.sailhero.model.Region;
import put.sailhero.model.User;
import put.sailhero.model.Yacht;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrefUtils {

	public static final String TAG = "sailhero";

	private static final String PREF_USER = "pref_user";
	private static final String PREF_REGION = "pref_region";
	private static final String PREF_YACHT = "pref_yacht";

	private static final String PREF_WELCOME_DONE = "pref_welcome_done";

	public static void setUser(Context context, final User user) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String userJson = gson.toJson(user);

		sp.edit().putString(PREF_USER, userJson).commit();
	}

	public static User getUser(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String userJson = null;
		userJson = sp.getString(PREF_USER, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		User user = gson.fromJson(userJson, User.class);
		return user;
	}

	public static void setYacht(Context context, final Yacht yacht) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String yachtJson = gson.toJson(yacht);

		sp.edit().putString(PREF_YACHT, yachtJson).commit();
	}

	public static Yacht getYacht(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String yachtJson = null;
		yachtJson = sp.getString(PREF_YACHT, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Yacht yacht = gson.fromJson(yachtJson, Yacht.class);
		return yacht;
	}

	public static void setRegion(Context context, final Region region) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		String regionJson = gson.toJson(region);

		sp.edit().putString(PREF_REGION, regionJson).commit();
	}

	public static Region getRegion(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String regionJson = null;
		regionJson = sp.getString(PREF_REGION, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Region region = gson.fromJson(regionJson, Region.class);
		return region;
	}

	public static boolean isWelcomeDone(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(PREF_WELCOME_DONE, false);
	}

	public static void markWelcomeDone(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(PREF_WELCOME_DONE, true).commit();
	}

}
