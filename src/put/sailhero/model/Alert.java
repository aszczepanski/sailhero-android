package put.sailhero.model;

import org.json.simple.JSONObject;

import put.sailhero.provider.SailHeroContract;
import android.content.ContentValues;
import android.database.Cursor;
import android.location.Location;

public class Alert extends BaseModel {

	private Double mLatitude;
	private Double mLongitude;
	private String mAlertType;
	private String mAdditionalInfo;
	private Integer mUserId;
	private Integer mCredibility;

	public Alert() {
	}

	public Alert(JSONObject alertObject) {
		this();

		setId(Integer.valueOf(alertObject.get("id").toString()));
		setAlertType(alertObject.get("alert_type").toString());
		setAdditionalInfo(alertObject.get("additional_info").toString());
		setUserId(Integer.valueOf(alertObject.get("user_id").toString()));
		setCredibility(Integer.valueOf(alertObject.get("credibility").toString()));

		setLatitude(Double.valueOf(alertObject.get("latitude").toString()));
		setLongitude(Double.valueOf(alertObject.get("longitude").toString()));
	}

	public Alert(Cursor c) {
		setId(c.getInt(Query.ALERT_ID));
		setAlertType(c.getString(Query.ALERT_TYPE));
		setLatitude(c.getDouble(Query.ALERT_LATITUDE));
		setLongitude(c.getDouble(Query.ALERT_LONGITUDE));
		setAdditionalInfo(c.getString(Query.ALERT_ADDITIONAL_INFO));
		setUserId(c.getInt(Query.ALERT_USER_ID));
	}

	public interface Query {
		String[] PROJECTION = {
				SailHeroContract.Alert.COLUMN_NAME_ID,
				SailHeroContract.Alert.COLUMN_NAME_TYPE,
				SailHeroContract.Alert.COLUMN_NAME_LATITUDE,
				SailHeroContract.Alert.COLUMN_NAME_LONGITUDE,
				SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO,
				SailHeroContract.Alert.COLUMN_NAME_USER_ID
		};

		int ALERT_ID = 0;
		int ALERT_TYPE = 1;
		int ALERT_LATITUDE = 2;
		int ALERT_LONGITUDE = 3;
		int ALERT_ADDITIONAL_INFO = 4;
		int ALERT_USER_ID = 5;
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject alertObject = new JSONObject();

		alertObject.put("alert_type", mAlertType);
		alertObject.put("latitude", mLatitude);
		alertObject.put("longitude", mLongitude);
		alertObject.put("additional_info", mAdditionalInfo);

		return alertObject;
	}

	public Double getLatitude() {
		return mLatitude;
	}

	public void setLatitude(Double latitude) {
		mLatitude = latitude;
	}

	public Double getLongitude() {
		return mLongitude;
	}

	public void setLongitude(Double longitude) {
		mLongitude = longitude;
	}

	public Location getLocation() {
		Location location = new Location("sailhero");
		location.setLatitude(getLatitude());
		location.setLongitude(getLongitude());

		return location;
	}

	public String getAlertType() {
		return mAlertType;
	}

	public void setAlertType(String alertType) {
		mAlertType = alertType;
	}

	public String getAdditionalInfo() {
		return mAdditionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		mAdditionalInfo = additionalInfo;
	}

	public Integer getUserId() {
		return mUserId;
	}

	public void setUserId(Integer userId) {
		mUserId = userId;
	}

	public Integer getCredibility() {
		return mCredibility;
	}

	public void setCredibility(Integer credibility) {
		mCredibility = credibility;
	}

	public ContentValues toContentValues() {
		ContentValues values = new ContentValues();
		values.put(SailHeroContract.Alert.COLUMN_NAME_ID, getId());
		values.put(SailHeroContract.Alert.COLUMN_NAME_TYPE, getAlertType());
		values.put(SailHeroContract.Alert.COLUMN_NAME_LATITUDE, getLatitude());
		values.put(SailHeroContract.Alert.COLUMN_NAME_LONGITUDE, getLongitude());
		values.put(SailHeroContract.Alert.COLUMN_NAME_USER_ID, getUserId());
		values.put(SailHeroContract.Alert.COLUMN_NAME_ADDITIONAL_INFO, getAdditionalInfo());

		return values;
	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof Alert)) {
			return false;
		}

		Alert alertToCompare = (Alert) o;

		return getId().equals(alertToCompare.getId()) && getLatitude().equals(alertToCompare.getLatitude())
				&& getLongitude().equals(alertToCompare.getLongitude())
				&& getAdditionalInfo().equals(alertToCompare.getAdditionalInfo())
				&& getUserId().equals(alertToCompare.getUserId());
	}
}
