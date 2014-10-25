package put.sailhero.android.util;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.util.model.Port;

public class GetPortsResponseCreator implements ResponseCreator<GetPortsResponse> {

	GetPortsResponse getPortsResponse = new GetPortsResponse();

	@Override
	public GetPortsResponse createFrom(HttpResponse response) throws InvalidResourceOwnerException,
			SystemException, UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				LinkedList<Port> ports = new LinkedList<Port>();

				JSONArray portsArray = (JSONArray) obj.get("ports");
				for (int i = 0; i < portsArray.size(); i++) {
					JSONObject portObject = (JSONObject) portsArray.get(i);
					Port port = new Port(portObject);

					ports.addLast(port);
				}

				getPortsResponse.setPorts(ports);

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {

		} else {
			throw new SystemException("Invalid status code");
		}

		return getPortsResponse;
	}

}
