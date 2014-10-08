package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class CreateYachtResponse extends ProcessedResponse {

	private Yacht yacht;
	
	@Override
	public void createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException,
			UnprocessableEntityException {
		int statusCode = response.getStatusCode();
		
		if (statusCode == 201) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());
				
				JSONObject yachtObject = (JSONObject) obj.get("yacht");
				yacht = new Yacht();
				yacht.setId(Integer.valueOf(yachtObject.get("id").toString()));
				yacht.setName(yachtObject.get("name").toString());
				yacht.setLength(Integer.valueOf(yachtObject.get("length").toString()));
				yacht.setWidth(Integer.valueOf(yachtObject.get("width").toString()));
				yacht.setCrew(Integer.valueOf(yachtObject.get("yacht_id").toString()));

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 422) {
			throw new UnprocessableEntityException(response.getBody());
		} else {
			throw new SystemException("Invalid status code");
		}
		
	}

	public Yacht getYacht() {
		return yacht;
	}

	public void setYacht(Yacht yacht) {
		this.yacht = yacht;
	}

}
