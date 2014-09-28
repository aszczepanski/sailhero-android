package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResponseException;

public class AuthenticateUserResponse extends ProcessedResponse {
	private String accessToken;
	private String tokenType;
	private Integer expiresIn;
	private String refreshToken;

	@Override
	protected void processOkStatusCode(HttpResponse response) throws InvalidResponseException {
		if (response.getStatusCode() == 200) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());
				
				setAccessToken(obj.get("access_token").toString());
				setTokenType(obj.get("token_type").toString());
				setExpiresIn(Integer.valueOf(obj.get("expires_in").toString()));
				setRefreshToken(obj.get("refresh_token").toString());

			} catch (NullPointerException e) {
				throw new InvalidResponseException(e.getMessage());
			} catch (ParseException e) {
				throw new InvalidResponseException(e.getMessage());
			}
		} else {
			throw new InvalidResponseException("Invalid status code");
		}
		
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return tokenType;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public Integer getExpiresIn() {
		return expiresIn;
	}

	public void setExpiresIn(Integer expiresIn) {
		this.expiresIn = expiresIn;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
