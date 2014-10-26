package put.sailhero.android.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnauthorizedException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.util.model.Alert;

public class CreateAlertResponseCreator implements ResponseCreator<CreateAlertResponse> {

	@Override
	public CreateAlertResponse createFrom(HttpResponse response) throws SystemException,
			UnprocessableEntityException, UnauthorizedException {
		CreateAlertResponse createAlertResponse = new CreateAlertResponse();

		int statusCode = response.getStatusCode();

		if (statusCode == 201) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject alertObject = (JSONObject) obj.get("alert");
				Alert alert = new Alert(alertObject);

				createAlertResponse.setAlert(alert);

			} catch (NullPointerException e) {
				throw new SystemException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new SystemException(e.getMessage());
			} catch (ParseException e) {
				throw new SystemException(e.getMessage());
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else if (statusCode == 422) {
			throw new UnprocessableEntityException(response.getBody());
		} else {
			throw new SystemException("Invalid status code");
		}

		return createAlertResponse;
	}
}
