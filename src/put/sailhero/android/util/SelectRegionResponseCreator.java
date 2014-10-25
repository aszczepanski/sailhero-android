package put.sailhero.android.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.util.model.Region;

public class SelectRegionResponseCreator implements ResponseCreator<SelectRegionResponse> {

	@Override
	public SelectRegionResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException, UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		SelectRegionResponse selectRegionResponse = new SelectRegionResponse();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject regionObject = (JSONObject) obj.get("region");
				Region region = new Region(regionObject);

				selectRegionResponse.setRegion(region);

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

		return selectRegionResponse;
	}

}
