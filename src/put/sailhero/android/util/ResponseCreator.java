package put.sailhero.android.util;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public interface ResponseCreator <T extends ProcessedResponse> {
	public T createFrom(HttpResponse response) throws InvalidResourceOwnerException, SystemException, UnprocessableEntityException; 
}
