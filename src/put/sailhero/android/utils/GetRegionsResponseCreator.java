package put.sailhero.android.utils;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class GetRegionsResponseCreator implements
		ResponseCreator<GetRegionsResponse> {

	@Override
	public GetRegionsResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException,
			UnprocessableEntityException {
		int statusCode = response.getStatusCode();
		
		GetRegionsResponse getRegionsResponse = new GetRegionsResponse();
		
		if (statusCode == 200) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				LinkedList<Region> regions = new LinkedList<Region>();
				
				JSONArray regionsArray = (JSONArray) obj.get("regions");
				for (int i=0; i<regionsArray.size(); i++) {
					JSONObject regionObject = (JSONObject) regionsArray.get(i);
					Region region = new Region();
					region.setId(Integer.valueOf(regionObject.get("id").toString()));
					region.setFullName(regionObject.get("full_name").toString());
					region.setCodeName(regionObject.get("code_name").toString());
					
					regions.addLast(region);
				}
				
				getRegionsResponse.setRegions(regions);

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

		return getRegionsResponse;
	}

}
