package put.sailhero.util;

import put.sailhero.R;
import android.content.Context;

public class StringUtils {
	public static final String ALERT_TYPE_CLOSED_AREA = "CLOSED_AREA";
	public static final String ALERT_TYPE_BAD_WEATHER_CONDITIONS = "BAD_WEATHER_CONDITIONS";
	public static final String ALERT_TYPE_YACHT_FAILURE = "YACHT_FAILURE";

	public static String getStringForAlertType(final Context context, String alertType) {
		if (alertType.equals(ALERT_TYPE_CLOSED_AREA)) {
			return context.getResources().getString(R.string.alert_name_closed_area);
		} else if (alertType.equals(ALERT_TYPE_BAD_WEATHER_CONDITIONS)) {
			return context.getResources().getString(R.string.alert_name_bad_weather_conditions);
		} else if (alertType.equals(ALERT_TYPE_YACHT_FAILURE)) {
			return context.getResources().getString(R.string.alert_name_yacht_failure);
		} else {
			return "N/A";
		}
	}
}
