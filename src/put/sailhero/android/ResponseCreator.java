package put.sailhero.android;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SailHeroSystemException;
import put.sailhero.android.exception.UnprocessableEntityException;


public class ResponseCreator {
	static public <T extends ProcessedResponse> T createFrom(HttpResponse response, Class<T> cls) throws InvalidResourceOwnerException, SailHeroSystemException, UnprocessableEntityException {
		T processedResponse;
		try {
			processedResponse = cls.newInstance();
			processedResponse.createFrom(response);
			return processedResponse;
		} catch (InstantiationException | IllegalAccessException e) {
			throw new SailHeroSystemException("Instantiation exception");
		}
	}
	
}
