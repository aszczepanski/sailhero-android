package put.sailhero.android;

import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.TransportException;

public interface Transport {
	public HttpResponse doRequest(Request request) throws InvalidRequestException, TransportException;
}
