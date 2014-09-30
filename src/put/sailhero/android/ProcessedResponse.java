package put.sailhero.android;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;

public abstract class ProcessedResponse {
	public abstract void createFrom(HttpResponse response) throws InvalidResponseException, InvalidClientException, InvalidResourceOwnerException, UnsupportedGrantTypeException, InvalidRequestException;

}
