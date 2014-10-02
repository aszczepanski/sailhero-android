package put.sailhero.android;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SailHeroSystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public abstract class ProcessedResponse {
	public abstract void createFrom(HttpResponse response) throws InvalidResourceOwnerException, SailHeroSystemException, UnprocessableEntityException;

}
