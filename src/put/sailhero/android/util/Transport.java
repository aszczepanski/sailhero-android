package put.sailhero.android.util;

import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.TransportException;

public interface Transport {
	public HttpResponse doRequest(Request request) throws TransportException, SystemException;
}
