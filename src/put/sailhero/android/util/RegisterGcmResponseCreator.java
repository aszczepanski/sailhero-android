package put.sailhero.android.util;

import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnauthorizedException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class RegisterGcmResponseCreator implements ResponseCreator<CreateAlertResponse> {

	@Override
	public CreateAlertResponse createFrom(HttpResponse response) throws SystemException,
			UnprocessableEntityException, UnauthorizedException {
		CreateAlertResponse createAlertResponse = new CreateAlertResponse();

		int statusCode = response.getStatusCode();

		if (statusCode == 201) {
			// gcm id registered
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code");
		}

		return createAlertResponse;
	}
}
