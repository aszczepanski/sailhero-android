package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;

public class AuthenticateUserResponse extends ProcessedResponse {
	private String accessToken;
	private String tokenType;
	private Integer expiresIn;
	private String refreshToken;

	@Override
	public void createFrom(HttpResponse response) throws InvalidResourceOwnerException, SystemException {
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				setAccessToken(obj.get("access_token").toString());
				setTokenType(obj.get("token_type").toString());
				setExpiresIn(Integer.valueOf(obj.get("expires_in").toString()));
				setRefreshToken(obj.get("refresh_token").toString());

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			JSONParser parser = new JSONParser();
			JSONObject obj;
			try {
				obj = (JSONObject) parser.parse(response.getBody());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
			String error;
			String errorMessage;
			try {
				error = obj.get("error").toString();
				errorMessage = obj.get("error_description").toString();
			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			}
			if (!error.isEmpty()) {
				if (error.equalsIgnoreCase("invalid_resource_owner")) {
					throw new InvalidResourceOwnerException(errorMessage);
				} else {
					throw new SystemException(errorMessage);
				}
			} else {
				throw new SystemException("");
			}
		} else {
			throw new SystemException("Invalid status code");
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
