package put.sailhero.android;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;

public class UnauthorizeUserResponse extends ProcessedResponse {
	@Override
	public void createFrom(HttpResponse response) throws InvalidResponseException, InvalidClientException, InvalidResourceOwnerException, UnsupportedGrantTypeException, InvalidRequestException {
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			// token revoked
		} else {
			throw new InvalidResponseException("Invalid status code");
		}

	}
}
