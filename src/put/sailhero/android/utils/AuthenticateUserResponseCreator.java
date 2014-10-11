package put.sailhero.android.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class AuthenticateUserResponseCreator implements ResponseCreator<AuthenticateUserResponse> {

	@Override
	public AuthenticateUserResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException,
			UnprocessableEntityException {
		AuthenticateUserResponse authenticateUserResponse = new AuthenticateUserResponse();
		
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				authenticateUserResponse.setAccessToken(obj.get("access_token").toString());
				authenticateUserResponse.setTokenType(obj.get("token_type").toString());
				authenticateUserResponse.setExpiresIn(Integer.valueOf(obj.get("expires_in").toString()));
				authenticateUserResponse.setRefreshToken(obj.get("refresh_token").toString());

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
		
		return authenticateUserResponse;
	}

}
