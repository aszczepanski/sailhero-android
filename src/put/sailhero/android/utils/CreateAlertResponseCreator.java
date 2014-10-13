package put.sailhero.android.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;
import android.location.Location;

public class CreateAlertResponseCreator implements ResponseCreator<CreateAlertResponse> {

	@Override
	public CreateAlertResponse createFrom(HttpResponse response) throws SystemException, UnprocessableEntityException {
		CreateAlertResponse createAlertResponse = new CreateAlertResponse();

		int statusCode = response.getStatusCode();

		if (statusCode == 201) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject alertObject = (JSONObject) obj.get("alert");
				Alert alert = new Alert();
				alert.setId(Integer.valueOf(alertObject.get("id").toString()));
				alert.setAlertType(alertObject.get("alert_type").toString());
				alert.setAdditionalInfo(alertObject.get("additional_info").toString());
				alert.setCredibility(Integer.valueOf(alertObject.get("credibility").toString()));
				
				Location alertLocation = new Location("sailhero");
				alertLocation.setLatitude(Double.valueOf(alertObject.get("latitude").toString()));
				alertLocation.setLongitude(Double.valueOf(alertObject.get("longitude").toString()));		
				alert.setLocation(alertLocation);
				
				createAlertResponse.setAlert(alert);

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
		
		return createAlertResponse;
	}
}
