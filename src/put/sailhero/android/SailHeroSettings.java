package put.sailhero.android;

public abstract class SailHeroSettings {

	protected String appId;
	protected String appSecret;
	
	protected String accessTokenHost;
	protected String accessTokenPath;
	protected String accessToken;
	
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
