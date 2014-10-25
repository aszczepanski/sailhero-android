package put.sailhero.android.util;

import java.util.AbstractList;

import put.sailhero.android.util.model.Alert;
import put.sailhero.android.util.model.Port;
import put.sailhero.android.util.model.Region;
import put.sailhero.android.util.model.User;
import put.sailhero.android.util.model.Yacht;

public abstract class SailHeroSettings {

	protected String appId;
	protected String appSecret;

	protected String accessTokenHost;
	protected String accessTokenPath;
	protected String accessToken;
	protected String refreshToken;

	protected String apiHost;
	protected String apiPath;
	protected String version;
	protected String i18n;

	protected User user;
	protected Yacht yacht;
	protected Region region;

	protected AbstractList<Alert> alerts;
	protected AbstractList<Port> ports;
	protected AbstractList<Region> regionsList;

	abstract public void save();

	abstract public void clear();

	public String getApiPath() {
		return apiPath;
	}

	public void setApiPath(String apiPath) {
		this.apiPath = apiPath;
	}

	public String getApiHost() {
		return apiHost;
	}

	public void setApiHost(String apiHost) {
		this.apiHost = apiHost;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getI18n() {
		return i18n;
	}

	public void setI18n(String i18n) {
		this.i18n = i18n;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String getAccessTokenHost() {
		return accessTokenHost;
	}

	public void setAccessTokenHost(String accessTokenHost) {
		this.accessTokenHost = accessTokenHost;
	}

	public String getAccessTokenPath() {
		return accessTokenPath;
	}

	public void setAccessTokenPath(String accessTokenPath) {
		this.accessTokenPath = accessTokenPath;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Yacht getYacht() {
		return yacht;
	}

	public void setYacht(Yacht yacht) {
		this.yacht = yacht;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public AbstractList<Region> getRegionsList() {
		return regionsList;
	}

	public void setRegionsList(AbstractList<Region> regionsList) {
		this.regionsList = regionsList;
	}

	public AbstractList<Alert> getAlerts() {
		return alerts;
	}

	public void setAlerts(AbstractList<Alert> alerts) {
		this.alerts = alerts;
	}

	public AbstractList<Port> getPorts() {
		return ports;
	}

	public void setPorts(AbstractList<Port> ports) {
		this.ports = ports;
	}

}
