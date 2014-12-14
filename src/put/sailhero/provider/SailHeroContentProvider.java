package put.sailhero.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class SailHeroContentProvider extends ContentProvider {
	private final static String TAG = "sailhero";

	private SailHeroDatabaseHelper mSailHeroDatabaseHelper;

	private static final int ROUTE_ALERTS = 0;
	private static final int ROUTE_ALERTS_ID = 1;

	private static final int ROUTE_REGIONS = 10;
	private static final int ROUTE_REGIONS_ID = 11;

	private static final int ROUTE_PORTS = 20;
	private static final int ROUTE_PORTS_ID = 21;

	private static final int ROUTE_FRIENDSHIPS = 30;
	private static final int ROUTE_FRIENDSHIPS_ID = 31;

	private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "alerts", ROUTE_ALERTS);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "alerts/#", ROUTE_ALERTS_ID);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "regions", ROUTE_REGIONS);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "regions/#", ROUTE_REGIONS_ID);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "ports", ROUTE_PORTS);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "ports/#", ROUTE_PORTS_ID);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "friendships", ROUTE_FRIENDSHIPS);
		sUriMatcher.addURI(SailHeroContract.CONTENT_AUTHORITY, "friendships/#", ROUTE_FRIENDSHIPS_ID);
	}

	public SailHeroContentProvider() {
	}

	@Override
	public boolean onCreate() {
		mSailHeroDatabaseHelper = new SailHeroDatabaseHelper(getContext());
		return true;
	}

	@Override
	public String getType(Uri uri) {
		final int match = sUriMatcher.match(uri);
		switch (match) {
		case ROUTE_ALERTS:
			return SailHeroContract.Alert.CONTENT_TYPE;
		case ROUTE_ALERTS_ID:
			return SailHeroContract.Alert.CONTENT_ITEM_TYPE;
		case ROUTE_REGIONS:
			return SailHeroContract.Region.CONTENT_TYPE;
		case ROUTE_REGIONS_ID:
			return SailHeroContract.Region.CONTENT_ITEM_TYPE;
		case ROUTE_PORTS:
			return SailHeroContract.Port.CONTENT_TYPE;
		case ROUTE_PORTS_ID:
			return SailHeroContract.Port.CONTENT_ITEM_TYPE;
		case ROUTE_FRIENDSHIPS:
			return SailHeroContract.Friendship.CONTENT_TYPE;
		case ROUTE_FRIENDSHIPS_ID:
			return SailHeroContract.Friendship.CONTENT_ITEM_TYPE;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SelectionBuilder builder = new SelectionBuilder();
		final SQLiteDatabase db = mSailHeroDatabaseHelper.getWritableDatabase();

		String id;
		int count;

		int match = sUriMatcher.match(uri);
		switch (match) {
		case ROUTE_ALERTS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Alert.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.delete(db);
			break;
		case ROUTE_REGIONS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Region.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.delete(db);
			break;
		case ROUTE_PORTS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Port.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.delete(db);
			break;
		case ROUTE_FRIENDSHIPS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Friendship.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.delete(db);
			break;
		case ROUTE_ALERTS:
			count = builder.table(SailHeroContract.Alert.TABLE_NAME).where(selection, selectionArgs).delete(db);
			break;
		case ROUTE_REGIONS:
			count = builder.table(SailHeroContract.Region.TABLE_NAME).where(selection, selectionArgs).delete(db);
			break;
		case ROUTE_PORTS:
			count = builder.table(SailHeroContract.Port.TABLE_NAME).where(selection, selectionArgs).delete(db);
			break;
		case ROUTE_FRIENDSHIPS:
			count = builder.table(SailHeroContract.Friendship.TABLE_NAME).where(selection, selectionArgs).delete(db);
			break;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (count > 0) {
			Context ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);
		}

		return count;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		final SQLiteDatabase db = mSailHeroDatabaseHelper.getWritableDatabase();

		long id;
		Uri result;

		Context ctx;

		int match = sUriMatcher.match(uri);
		switch (match) {
		case ROUTE_ALERTS:
			id = db.insertOrThrow(SailHeroContract.Alert.TABLE_NAME, null, values);

			result = Uri.parse(SailHeroContract.Alert.CONTENT_URI + "/" + id);

			// TODO: notify whole table uri
			ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);

			return result;
		case ROUTE_REGIONS:
			id = db.insertOrThrow(SailHeroContract.Region.TABLE_NAME, null, values);

			result = Uri.parse(SailHeroContract.Region.CONTENT_URI + "/" + id);

			ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);

			return result;
		case ROUTE_PORTS:
			id = db.insertOrThrow(SailHeroContract.Port.TABLE_NAME, null, values);

			result = Uri.parse(SailHeroContract.Port.CONTENT_URI + "/" + id);

			ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);

			return result;
		case ROUTE_FRIENDSHIPS:
			id = db.insertOrThrow(SailHeroContract.Friendship.TABLE_NAME, null, values);

			result = Uri.parse(SailHeroContract.Friendship.CONTENT_URI + "/" + id);

			ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);

			return result;
		case ROUTE_ALERTS_ID:
		case ROUTE_REGIONS_ID:
		case ROUTE_PORTS_ID:
		case ROUTE_FRIENDSHIPS_ID:
			throw new UnsupportedOperationException("Insert not supported on URI: " + uri);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Log.i(TAG, uri.getPath());
		SQLiteDatabase db = mSailHeroDatabaseHelper.getReadableDatabase();
		SelectionBuilder builder = new SelectionBuilder();

		String id = "";
		Context ctx;
		Cursor cursor;
		int match = sUriMatcher.match(uri);

		switch (match) {
		case ROUTE_ALERTS_ID:
			id = uri.getLastPathSegment();
			builder.where("id" + "=?", id);
		case ROUTE_ALERTS:
			builder.table(SailHeroContract.Alert.TABLE_NAME).where(selection, selectionArgs);
			cursor = builder.query(db, projection, sortOrder);

			// TODO: make sure this is accurate
			ctx = getContext();
			assert ctx != null;
			cursor.setNotificationUri(ctx.getContentResolver(), uri);

			return cursor;
		case ROUTE_REGIONS_ID:
			id = uri.getLastPathSegment();
			builder.where("id" + "=?", id);
		case ROUTE_REGIONS:
			builder.table(SailHeroContract.Region.TABLE_NAME).where(selection, selectionArgs);
			cursor = builder.query(db, projection, sortOrder);

			// TODO: make sure this is accurate
			ctx = getContext();
			assert ctx != null;
			cursor.setNotificationUri(ctx.getContentResolver(), uri);

			return cursor;
		case ROUTE_PORTS_ID:
			id = uri.getLastPathSegment();
			builder.where("id" + "=?", id);
		case ROUTE_PORTS:
			builder.table(SailHeroContract.Port.TABLE_NAME).where(selection, selectionArgs);
			cursor = builder.query(db, projection, sortOrder);

			// TODO: make sure this is accurate
			ctx = getContext();
			assert ctx != null;
			cursor.setNotificationUri(ctx.getContentResolver(), uri);

			return cursor;
		case ROUTE_FRIENDSHIPS_ID:
			id = uri.getLastPathSegment();
			builder.where("id" + "=?", id);
		case ROUTE_FRIENDSHIPS:
			builder.table(SailHeroContract.Friendship.TABLE_NAME).where(selection, selectionArgs);
			cursor = builder.query(db, projection, sortOrder);

			// TODO: make sure this is accurate
			ctx = getContext();
			assert ctx != null;
			cursor.setNotificationUri(ctx.getContentResolver(), uri);

			return cursor;
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SelectionBuilder builder = new SelectionBuilder();
		SQLiteDatabase db = mSailHeroDatabaseHelper.getWritableDatabase();

		String id;
		int count;

		int match = sUriMatcher.match(uri);
		switch (match) {
		case ROUTE_ALERTS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Alert.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.update(db, values);

			break;
		case ROUTE_REGIONS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Region.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.update(db, values);

			break;
		case ROUTE_PORTS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Port.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.update(db, values);

			break;
		case ROUTE_FRIENDSHIPS_ID:
			id = uri.getLastPathSegment();
			count = builder.table(SailHeroContract.Friendship.TABLE_NAME)
					.where("id" + "=?", id)
					.where(selection, selectionArgs)
					.update(db, values);
			break;
		case ROUTE_ALERTS:
		case ROUTE_REGIONS:
		case ROUTE_PORTS:
		case ROUTE_FRIENDSHIPS:
			throw new UnsupportedOperationException("Update not supported on URI: " + uri);
		default:
			throw new UnsupportedOperationException("Unknown uri: " + uri);
		}

		if (count > 0) {
			Context ctx = getContext();
			assert ctx != null;
			ctx.getContentResolver().notifyChange(uri, null, false);
		}

		return count;
	}

	static class SailHeroDatabaseHelper extends SQLiteOpenHelper {
		public static final int DATABASE_VERSION = 20;
		public static final String DATABASE_NAME = "sailhero.db";

		private static final String SQL_CREATE_ALERTS = "CREATE TABLE " + SailHeroContract.Alert.TABLE_NAME + " ("
				+ SailHeroContract.Alert.COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + ","
				+ SailHeroContract.Alert.COLUMN_NAME_TYPE + " TEXT" + "," + SailHeroContract.Alert.COLUMN_NAME_LATITUDE
				+ " REAL" + "," + SailHeroContract.Alert.COLUMN_NAME_LONGITUDE + " REAL" + ","
				+ SailHeroContract.Alert.COLUMN_NAME_USER_ID + " INTEGER" + ","
				+ SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO + " TEXT" + ","
				+ SailHeroContract.Alert.COLUMN_NAME_RESPONSE_STATUS + " INTEGER" + " DEFAULT "
				+ SailHeroContract.Alert.RESPONSE_STATUS_NOT_RESPONDED + ")";

		private static final String SQL_DELETE_ALERTS = "DROP TABLE IF EXISTS " + SailHeroContract.Alert.TABLE_NAME;

		private static final String SQL_CREATE_REGIONS = "CREATE TABLE " + SailHeroContract.Region.TABLE_NAME + " ("
				+ SailHeroContract.Region.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
				+ SailHeroContract.Region.COLUMN_NAME_CODE_NAME + " TEXT" + ","
				+ SailHeroContract.Region.COLUMN_NAME_FULL_NAME + " TEXT" + ")";

		private static final String SQL_DELETE_REGIONS = "DROP TABLE IF EXISTS " + SailHeroContract.Region.TABLE_NAME;

		private static final String SQL_CREATE_PORTS = "CREATE TABLE " + SailHeroContract.Port.TABLE_NAME + " ("
				+ SailHeroContract.Port.COLUMN_NAME_ID + " INTEGER PRIMARY KEY" + ","
				+ SailHeroContract.Port.COLUMN_NAME_NAME + " TEXT" + "," + SailHeroContract.Port.COLUMN_NAME_LATITUDE
				+ " REAL" + "," + SailHeroContract.Port.COLUMN_NAME_LONGITUDE + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_WEBSITE + " TEXT" + "," + SailHeroContract.Port.COLUMN_NAME_CITY
				+ " TEXT" + "," + SailHeroContract.Port.COLUMN_NAME_STREET + " TEXT" + ","
				+ SailHeroContract.Port.COLUMN_NAME_TELEPHONE + " TEXT" + ","
				+ SailHeroContract.Port.COLUMN_NAME_ADDITIONAL_INFO + " TEXT" + ","
				+ SailHeroContract.Port.COLUMN_NAME_SPOTS + " INTEGER" + "," + SailHeroContract.Port.COLUMN_NAME_DEPTH
				+ " INTEGER" + "," + SailHeroContract.Port.COLUMN_NAME_HAS_POWER_CONNECTION + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_WC + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_SHOWER + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_WASHBASIN + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_DISHES + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_WIFI + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_PARKING + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_SLIP + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_WASHING_MACHINE + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_FUEL_STATION + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_HAS_EMPTYING_CHEMICAL_TOILET + " INTEGER" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_PER_PERSON + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_POWER_CONNECTION + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_WC + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_SHOWER + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_WASHBASIN + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_DISHES + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_WIFI + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_WASHING_MACHINE + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_EMPTYING_CHEMICAL_TOILET + " REAL" + ","
				+ SailHeroContract.Port.COLUMN_NAME_PRICE_PARKING + " REAL" + ")";

		private static final String SQL_DELETE_PORTS = "DROP TABLE IF EXISTS " + SailHeroContract.Port.TABLE_NAME;

		private static final String SQL_CREATE_FRIENDSHIPS = "CREATE TABLE " + SailHeroContract.Friendship.TABLE_NAME
				+ " (" + SailHeroContract.Friendship.COLUMN_NAME_ID + " INTEGER PRIMARY KEY,"
				+ SailHeroContract.Friendship.COLUMN_NAME_STATUS + " INTEGER" + ","
				+ SailHeroContract.Friendship.COLUMN_NAME_FRIEND_ID + " INTEGER" + ","
				+ SailHeroContract.Friendship.COLUMN_NAME_FRIEND_EMAIL + " TEXT" + ","
				+ SailHeroContract.Friendship.COLUMN_NAME_FRIEND_NAME + " TEXT" + ","
				+ SailHeroContract.Friendship.COLUMN_NAME_FRIEND_SURNAME + " TEXT" + ","
				+ SailHeroContract.Friendship.COLUMN_NAME_FRIEND_AVATAR_URL + " TEXT" + ")";

		private static final String SQL_DELETE_FRIENDSHIPS = "DROP TABLE IF EXISTS "
				+ SailHeroContract.Friendship.TABLE_NAME;

		public SailHeroDatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(SQL_CREATE_ALERTS);
			db.execSQL(SQL_CREATE_REGIONS);
			db.execSQL(SQL_CREATE_PORTS);
			db.execSQL(SQL_CREATE_FRIENDSHIPS);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL(SQL_DELETE_ALERTS);
			db.execSQL(SQL_DELETE_REGIONS);
			db.execSQL(SQL_DELETE_PORTS);
			db.execSQL(SQL_DELETE_FRIENDSHIPS);
			onCreate(db);
		}
	}
}
