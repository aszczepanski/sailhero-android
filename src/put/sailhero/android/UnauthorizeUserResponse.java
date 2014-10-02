package put.sailhero.android;

import put.sailhero.android.exception.SailHeroSystemException;

public class UnauthorizeUserResponse extends ProcessedResponse {
	@Override
	public void createFrom(HttpResponse response) throws SailHeroSystemException {
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			// token revoked
		} else {
			throw new SailHeroSystemException("Invalid status code");
		}

	}
}
