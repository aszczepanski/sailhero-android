package put.sailhero.android;

public abstract class SailHeroSettings {

	protected String appId;
	protected String appSecret;
	
	protected String accessTokenHost;
	protected String accessTokenPath;
	protected String accessToken;
	
	protected String apiHost;
	protected String apiPath;
	protected String version;
	protected String i18n;
	
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
	
}
