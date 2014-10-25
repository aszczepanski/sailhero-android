package put.sailhero.android.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class YachtResponseCreator implements ResponseCreator<YachtResponse> {

	@Override
	public YachtResponse createFrom(HttpResponse response) throws InvalidResourceOwnerException,
			SystemException, UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		YachtResponse createYachtResponse = new YachtResponse();

		if (statusCode == 200 || statusCode == 201) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject yachtObject = (JSONObject) obj.get("yacht");
				Yacht yacht = new Yacht(yachtObject);

				createYachtResponse.setYacht(yacht);

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

		return createYachtResponse;
	}

}
