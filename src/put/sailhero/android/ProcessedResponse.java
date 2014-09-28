package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;

public abstract class ProcessedResponse {
	public void createFrom(HttpResponse response) throws Exception {
		if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
			processOkStatusCode(response);
		} else {
			processErrorStatusCode(response);
		}
	}
	
	protected abstract void processOkStatusCode(HttpResponse response) throws InvalidResponseException;
	
	protected void processErrorStatusCode(HttpResponse response) throws Exception {
		if (response.getStatusCode() == 401) {
			JSONParser parser = new JSONParser();
			JSONObject obj = (JSONObject) parser.parse(response.getBody());
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
					throw new InvalidRequestException();
				}
			} else {
				throw new InvalidResponseException();
			}
		} else {
			// TODO: throw exceptions
			throw new InvalidRequestException();
		}
	}
}
