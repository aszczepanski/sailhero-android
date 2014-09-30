package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;

public class AuthenticateUserResponse extends ProcessedResponse {
	private String accessToken;
	private String tokenType;
	private Integer expiresIn;
	private String refreshToken;

	@Override
	public void createFrom(HttpResponse response) throws InvalidResponseException, InvalidClientException, InvalidResourceOwnerException, UnsupportedGrantTypeException, InvalidRequestException {
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
				throw new InvalidResponseException(e.getMessage());
			} catch (ParseException e) {
				throw new InvalidResponseException(e.getMessage());
			}
		} else if (statusCode == 401) {
			JSONParser parser = new JSONParser();
			JSONObject obj;
			try {
				obj = (JSONObject) parser.parse(response.getBody());
			} catch (ParseException e) {
				throw new InvalidResponseException(e.getMessage());
			}
			String error;
			String errorMessage;
			try {
				error = obj.get("error").toString();
				errorMessage = obj.get("error_description").toString();
			} catch (NullPointerException e) {
				throw new InvalidResponseException(e.getMessage());
			}
			if (!error.isEmpty()) {
				if (error.equalsIgnoreCase("invalid_client")) {
					throw new InvalidClientException(errorMessage);
				} else if (error.equalsIgnoreCase("invalid_resource_owner")) {
					throw new InvalidResourceOwnerException(errorMessage);
				} else if (error.equalsIgnoreCase("unsupported_grant_type")) {
					throw new UnsupportedGrantTypeException(errorMessage);
				} else if (error.equalsIgnoreCase("invalid_request")) {
					throw new InvalidRequestException(errorMessage);
				} else {
					// TODO: throw another exception
					throw new InvalidRequestException("");
				}
			} else {
				throw new InvalidResponseException("");
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
