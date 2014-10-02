package put.sailhero.android;

import put.sailhero.android.exception.SystemException;

public class UnauthorizeUserResponse extends ProcessedResponse {
	@Override
	public void createFrom(HttpResponse response) throws SystemException {
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			// token revoked
		} else {
			throw new SystemException("Invalid status code");
		}

	}
}
