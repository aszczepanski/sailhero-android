package put.sailhero.android.util;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class Connection {
	Transport transport = new ApacheHttpConnectionTransport();
	
	public <T extends ProcessedResponse> T send  (Request request, ResponseCreator<T> responseCreator) throws TransportException, InvalidResourceOwnerException, SystemException, UnprocessableEntityException {
		HttpResponse httpResponse = transport.doRequest(request);
		T processedResponse = responseCreator.createFrom(httpResponse);
		return processedResponse;
	}
}
