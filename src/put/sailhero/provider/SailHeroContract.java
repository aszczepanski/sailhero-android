package put.sailhero.provider;

import android.content.ContentResolver;
import android.net.Uri;

final public class SailHeroContract {
	private SailHeroContract() {
	}

	public static final String CONTENT_AUTHORITY = "put.sailhero";

	public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

	private static final String PATH_ALERTS = "alerts";
	private static final String PATH_REGIONS = "regions";
	private static final String PATH_PORTS = "ports";
	private static final String PATH_FRIENDSHIPS = "friendships";
	private static final String PATH_ROUTES = "routes";
	private static final String PATH_PINS = "pins";

	public static class Alert {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.put.sailhero.alerts";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.put.sailhero.alert";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ALERTS).build();

		public static final String TABLE_NAME = "alerts";

		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_TYPE = "type";
		public static final String COLUMN_NAME_LATITUDE = "latitude";
		public static final String COLUMN_NAME_LONGITUDE = "longitude";
		public static final String COLUMN_NAME_USER_ID = "user_id";
		public static final String COLUMN_NAME_ADDITIONAL_INFO = "additional_info";
		public static final String COLUMN_NAME_USER_RESPONDED = "user_responded";

		public static final int RESPONSE_STATUS_NOT_RESPONDED = 0;
		public static final int RESPONSE_STATUS_RESPONDED = 1;
	}

	public static class Region {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.put.sailhero.regions";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.put.sailhero.region";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_REGIONS).build();

		public static final String TABLE_NAME = "regions";

		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_CODE_NAME = "code_name";
		public static final String COLUMN_NAME_FULL_NAME = "full_name";
	}

	public static class Port {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.put.sailhero.ports";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.put.sailhero.ports";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PORTS).build();

		public static final String TABLE_NAME = "ports";

		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_NAME = "name";
		public static final String COLUMN_NAME_LONGITUDE = "longitude";
		public static final String COLUMN_NAME_LATITUDE = "latitude";
		public static final String COLUMN_NAME_WEBSITE = "website";
		public static final String COLUMN_NAME_CITY = "city";
		public static final String COLUMN_NAME_STREET = "street";
		public static final String COLUMN_NAME_TELEPHONE = "telephone";
		public static final String COLUMN_NAME_ADDITIONAL_INFO = "additional_info";
		public static final String COLUMN_NAME_SPOTS = "spots";
		public static final String COLUMN_NAME_DEPTH = "depth";
		public static final String COLUMN_NAME_PHOTO_URL = "photo_url";
		public static final String COLUMN_NAME_HAS_POWER_CONNECTION = "has_power_connection";
		public static final String COLUMN_NAME_HAS_WC = "has_wc";
		public static final String COLUMN_NAME_HAS_SHOWER = "has_shower";
		public static final String COLUMN_NAME_HAS_WASHBASIN = "has_washbasin";
		public static final String COLUMN_NAME_HAS_DISHES = "has_dishes";
		public static final String COLUMN_NAME_HAS_WIFI = "has_wifi";
		public static final String COLUMN_NAME_HAS_PARKING = "has_parking";
		public static final String COLUMN_NAME_HAS_SLIP = "has_slip";
		public static final String COLUMN_NAME_HAS_WASHING_MACHINE = "has_washing_machine";
		public static final String COLUMN_NAME_HAS_FUEL_STATION = "has_fuel_station";
		public static final String COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET = "has_emptying_chemical_toilet";
		public static final String COLUMN_NAME_PRICE_PER_PERSON = "price_per_person";
		public static final String COLUMN_NAME_PRICE_POWER_CONNECTION = "price_power_connection";
		public static final String COLUMN_NAME_PRICE_WC = "price_wc";
		public static final String COLUMN_NAME_PRICE_SHOWER = "price_shower";
		public static final String COLUMN_NAME_PRICE_WASHBASIN = "price_washbasin";
		public static final String COLUMN_NAME_PRICE_DISHES = "price_dishes";
		public static final String COLUMN_NAME_PRICE_WIFI = "price_wifi";
		public static final String COLUMN_NAME_PRICE_PARKING = "price_parking";
		public static final String COLUMN_NAME_PRICE_WASHING_MACHINE = "price_washing_machine";
		public static final String COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET = "price_emptying_chemical_toilet";
		public static final String COLUMN_NAME_CURRENCY = "currency";
	}

	public static class Friendship {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
				+ "/vnd.put.sailhero.friendships";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.put.sailhero.friendship";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FRIENDSHIPS).build();

		public static final String TABLE_NAME = "friendships";

		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_STATUS = "status";
		public static final String COLUMN_NAME_FRIEND_ID = "friend_id";
		public static final String COLUMN_NAME_FRIEND_EMAIL = "friend_email";
		public static final String COLUMN_NAME_FRIEND_NAME = "friend_name";
		public static final String COLUMN_NAME_FRIEND_SURNAME = "friend_surname";
		public static final String COLUMN_NAME_FRIEND_AVATAR_URL = "friend_avatar_url";
		public static final String COLUMN_NAME_FRIEND_LATITUDE = "friend_latitude";
		public static final String COLUMN_NAME_FRIEND_LONGITUDE = "friend_longitude";

		public static final int STATUS_STRANGER = -1;
		public static final int STATUS_ACCEPTED = 0;
		public static final int STATUS_PENDING = 1;
		public static final int STATUS_SENT = 2;
	}

	public static class Route {
		public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.put.sailhero.routes";
		public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
				+ "/vnd.put.sailhero.route";

		public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_ROUTES).build();

		public static final String TABLE_NAME = "routes";

		public static final String COLUMN_NAME_ID = "id";
		public static final String COLUMN_NAME_NAME = "name";

		public static class Pin {
			public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.put.sailhero.pins";
			public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE
					+ "/vnd.put.sailhero.pin";

			public static final String CONTENT_JOIN_ROUTES_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE
					+ "/vnd.put.sailhero.routes.pins";

			public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_PINS).build();
			public static final Uri CONTENT_JOIN_ROUTES_URI = BASE_CONTENT_URI.buildUpon()
					.appendPath("routes_pins")
					.build();

			public static final String TABLE_NAME = "pins";
			public static final String ROUTES_VIEW_NAME = "routes_pins";

			public static final String COLUMN_NAME_ROUTE_ID = "route_id";
			public static final String COLUMN_NAME_POSITION_IN_ROUTE = "position_in_route";
			public static final String COLUMN_NAME_LATITUDE = "latitude";
			public static final String COLUMN_NAME_LONGITUDE = "longitude";
		}
	}

}
