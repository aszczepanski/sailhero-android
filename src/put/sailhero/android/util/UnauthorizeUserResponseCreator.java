package put.sailhero.android.util;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class UnauthorizeUserResponseCreator implements ResponseCreator<UnauthorizeUserResponse> {

	@Override
	public UnauthorizeUserResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException,
			UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		UnauthorizeUserResponse unauthorizeUserResponse = new UnauthorizeUserResponse();
		
		if (statusCode == 200) {
			// token revoked
		} else {
			throw new SystemException("Invalid status code");
		}
		
		return unauthorizeUserResponse;
	}

}
