package put.sailhero.android;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;


public class ResponseCreator {
	static public <T extends ProcessedResponse> T createFrom(HttpResponse response, Class<T> cls) throws InstantiationException, IllegalAccessException, InvalidResponseException, InvalidClientException, InvalidResourceOwnerException, UnsupportedGrantTypeException, InvalidRequestException {
		T processedResponse = cls.newInstance();
		
		processedResponse.createFrom(response);
		
		return processedResponse;
	}
	
}
