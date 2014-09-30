package put.sailhero.android;

import put.sailhero.android.exception.InvalidClientException;
import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.InvalidResponseException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnsupportedGrantTypeException;

public class Connection {
	Transport transport = new ApacheHttpConnectionTransport();
	
	public <T extends ProcessedResponse> T send  (Request request, Class<T> cls) throws InvalidRequestException, TransportException, InvalidResponseException, InvalidClientException, InvalidResourceOwnerException, UnsupportedGrantTypeException, InstantiationException, IllegalAccessException {
		HttpResponse httpResponse = transport.doRequest(request);
		T processedResponse = ResponseCreator.createFrom(httpResponse, cls);
		return processedResponse;
	}
}
