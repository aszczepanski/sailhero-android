package put.sailhero.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import put.sailhero.android.exception.InvalidRequestException;
import put.sailhero.android.exception.TransportException;
import android.util.Log;

public class ApacheHttpConnectionTransport implements Transport {
	public final static String TAG = "sailhero";

	HttpClient httpClient = new DefaultHttpClient();

	@Override
	public HttpResponse doRequest(Request request) throws InvalidRequestException, TransportException {
		HttpResponse response = null;
		try {
			HttpUriRequest httpUriRequest = createHttpUriRequestFrom(request);

			Log.d(TAG, "Executing request " + httpUriRequest.getRequestLine());

			response = httpClient.execute(httpUriRequest, new ResponseHandler<HttpResponse>() {

				@Override
				public HttpResponse handleResponse(org.apache.http.HttpResponse receivedResponse)
						throws ClientProtocolException, IOException {
					HttpResponse response = new HttpResponse();
					response.setBody(EntityUtils.toString(receivedResponse.getEntity()));
					response.setStatusCode(receivedResponse.getStatusLine().getStatusCode());

					return response;
				}	
			});
			Log.d(TAG, "----------------------------------------");
			Log.d(TAG, "status: " + response.getStatusCode());
			Log.d(TAG, response.getBody());

		} catch (URISyntaxException e) {
			throw new InvalidRequestException(e.getMessage());
		} catch (UnsupportedEncodingException e) {
			throw new InvalidRequestException(e.getMessage());
		} catch (ClientProtocolException e) {
			throw new TransportException(e.getMessage());
		} catch (IOException e) {
			throw new TransportException(e.getMessage());
		}
		return response;
	}

	private HttpUriRequest createHttpUriRequestFrom(Request request) throws URISyntaxException, UnsupportedEncodingException {
		URI uri = new URI(request.getUrl());

		HttpUriRequest httpUriRequest = null;

		// TODO: set headers

		switch (request.getMethod()) {
		case GET:
			httpUriRequest = new HttpGet(uri);
			// httpUriRequest.setHeaders(request.getHeaders());
			break;
		case POST:
			httpUriRequest = new HttpPost(uri);
			// httpUriRequest.setHeaders(request.getHeaders());
			HttpEntity entity = new StringEntity(request.getBody());
			((HttpPost) httpUriRequest).setEntity(entity);
		}

		return httpUriRequest;
	}
}
