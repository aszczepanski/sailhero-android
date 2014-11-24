package put.sailhero.model;

import org.json.simple.JSONObject;

import android.location.Location;

public class Alert {

	private Integer id;
	private Location location;
	private String alertType;
	private String additionalInfo;
	private Integer credibility;

	public Alert() {
	}

	public Alert(JSONObject alertObject) {
		this();

		setId(Integer.valueOf(alertObject.get("id").toString()));
		setAlertType(alertObject.get("alert_type").toString());
		setAdditionalInfo(alertObject.get("additional_info").toString());
		setCredibility(Integer.valueOf(alertObject.get("credibility").toString()));

		Location alertLocation = new Location("sailhero");
		alertLocation.setLatitude(Double.valueOf(alertObject.get("latitude").toString()));
		alertLocation.setLongitude(Double.valueOf(alertObject.get("longitude").toString()));
		setLocation(alertLocation);
	}

	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject alertObject = new JSONObject();

		alertObject.put("alert_type", alertType);
		alertObject.put("latitude", location.getLatitude());
		alertObject.put("longitude", location.getLongitude());
		alertObject.put("additional_info", additionalInfo);

		return alertObject;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public String getAlertType() {
		return alertType;
	}

	public void setAlertType(String alertType) {
		this.alertType = alertType;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public Integer getCredibility() {
		return credibility;
	}

	public void setCredibility(Integer credibility) {
		this.credibility = credibility;
	}

}
