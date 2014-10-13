package put.sailhero.android.utils;

import android.location.Location;

public class Alert {

	private Integer id;
	private Location location;
	private String alertType;
	private String additionalInfo;
	private Integer credibility;

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
