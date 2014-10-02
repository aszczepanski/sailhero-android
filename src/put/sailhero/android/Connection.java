package put.sailhero.android;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SailHeroSystemException;
import put.sailhero.android.exception.TransportException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class Connection {
	Transport transport = new ApacheHttpConnectionTransport();
	
	public <T extends ProcessedResponse> T send  (Request request, Class<T> cls) throws TransportException, InvalidResourceOwnerException, SailHeroSystemException, UnprocessableEntityException {
		HttpResponse httpResponse = transport.doRequest(request);
		T processedResponse = ResponseCreator.createFrom(httpResponse, cls);
		return processedResponse;
	}
}
