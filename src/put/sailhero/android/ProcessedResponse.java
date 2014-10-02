package put.sailhero.android;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public abstract class ProcessedResponse {
	public abstract void createFrom(HttpResponse response) throws InvalidResourceOwnerException, SystemException, UnprocessableEntityException;

}
