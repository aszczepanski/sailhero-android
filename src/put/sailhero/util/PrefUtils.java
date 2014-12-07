package put.sailhero.util;

import put.sailhero.model.Alert;
import put.sailhero.model.Region;
import put.sailhero.model.User;
import put.sailhero.model.Yacht;
import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class PrefUtils {

	public static final String TAG = "sailhero";

	public static final String PREF_USER = "pref_user";
	public static final String PREF_REGION = "pref_region";
	public static final String PREF_YACHT = "pref_yacht";

	public static final String PREF_ALERT_RADIUS = "pref_alert_radius";
	public static final String PREF_ALERT_TO_RESPOND = "pref_alert_to_respond";

	public static final String PREF_LAST_KNOWN_LOCATION = "pref_last_known_location";

	public static final String PREF_WELCOME_DONE = "pref_welcome_done";

	public static final String PREF_GCM_REG_ID = "pref_gcm_reg_id";

	public static final int DEFAULT_ALERT_RADIUS = 500;

	public static void clear(final Context context) {
		setUser(context, null);
		setYacht(context, null);
		setRegion(context, null);
		setAlertRadius(context, DEFAULT_ALERT_RADIUS);
		setAlertToRespond(context, null);
		setLastKnownLocation(context, null);
		// TODO: welcome done
		setGcmRegistrationId(context, null);
	}

	public static void setUser(final Context context, final User user) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		if (user == null) {
			sp.edit().putString(PREF_USER, null).commit();
		} else {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String userJson = gson.toJson(user);

			sp.edit().putString(PREF_USER, userJson).commit();
		}
	}

	public static User getUser(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String userJson = null;
		userJson = sp.getString(PREF_USER, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		User user = gson.fromJson(userJson, User.class);
		return user;
	}

	public static void setYacht(final Context context, final Yacht yacht) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		if (yacht == null) {
			sp.edit().putString(PREF_YACHT, null).commit();
		} else {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String yachtJson = gson.toJson(yacht);

			sp.edit().putString(PREF_YACHT, yachtJson).commit();
		}
	}

	public static Yacht getYacht(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String yachtJson = null;
		yachtJson = sp.getString(PREF_YACHT, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Yacht yacht = gson.fromJson(yachtJson, Yacht.class);
		return yacht;
	}

	public static void setRegion(final Context context, final Region region) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		if (region == null) {
			sp.edit().putString(PREF_REGION, null).commit();
		} else {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String regionJson = gson.toJson(region);

			sp.edit().putString(PREF_REGION, regionJson).commit();
		}
	}

	public static Region getRegion(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String regionJson = null;
		regionJson = sp.getString(PREF_REGION, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Region region = gson.fromJson(regionJson, Region.class);
		return region;
	}

	public static Integer getAlertRadius(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getInt(PREF_ALERT_RADIUS, DEFAULT_ALERT_RADIUS);
	}

	public static void setAlertRadius(final Context context, final Integer radius) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putInt(PREF_ALERT_RADIUS, radius).commit();
	}

	public static void setAlertToRespond(Context context, final Alert alert) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		if (alert == null) {
			sp.edit().putString(PREF_ALERT_TO_RESPOND, null).commit();
		} else {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String alertJson = gson.toJson(alert);

			sp.edit().putString(PREF_ALERT_TO_RESPOND, alertJson).commit();
		}
	}

	public static Alert getAlertToRespond(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String alertJson = null;
		alertJson = sp.getString(PREF_ALERT_TO_RESPOND, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Alert alert = gson.fromJson(alertJson, Alert.class);
		return alert;
	}

	public static void setLastKnownLocation(Context context, final Location location) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		if (location == null) {
			sp.edit().putString(PREF_LAST_KNOWN_LOCATION, null);
		} else {
			GsonBuilder builder = new GsonBuilder();
			Gson gson = builder.create();
			String locationJson = gson.toJson(location);

			sp.edit().putString(PREF_LAST_KNOWN_LOCATION, locationJson).commit();
		}
	}

	public static Location getLastKnownLocation(Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

		String locationJson = null;
		locationJson = sp.getString(PREF_LAST_KNOWN_LOCATION, null);

		GsonBuilder builder = new GsonBuilder();
		Gson gson = builder.create();
		Location location = gson.fromJson(locationJson, Location.class);
		return location;
	}

	public static boolean isWelcomeDone(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getBoolean(PREF_WELCOME_DONE, false);
	}

	public static void markWelcomeDone(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putBoolean(PREF_WELCOME_DONE, true).commit();
	}

	public static String getGcmRegistrationId(final Context context) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		return sp.getString(PREF_GCM_REG_ID, null);
	}

	public static void setGcmRegistrationId(final Context context, String registrationId) {
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		sp.edit().putString(PREF_GCM_REG_ID, registrationId).commit();
	}

}
