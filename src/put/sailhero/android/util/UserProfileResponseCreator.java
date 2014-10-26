package put.sailhero.android.util;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnauthorizedException;
import put.sailhero.android.exception.UnprocessableEntityException;
import put.sailhero.android.util.model.Region;
import put.sailhero.android.util.model.User;
import put.sailhero.android.util.model.Yacht;

public class UserProfileResponseCreator implements ResponseCreator<UserProfileResponse> {

	@Override
	public UserProfileResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException, UnprocessableEntityException,
			UnauthorizedException {
		int statusCode = response.getStatusCode();

		UserProfileResponse userProfileResponse = new UserProfileResponse();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject userObject = (JSONObject) obj.get("user");
				User user = new User(userObject);				
				userProfileResponse.setUser(user);
				
				JSONObject yachtObject = (JSONObject) userObject.get("yacht");
				if (yachtObject != null) {
					Yacht yacht = new Yacht(yachtObject);
					userProfileResponse.setYacht(yacht);
				} else {
					userProfileResponse.setYacht(null);
				}

				JSONObject regionObject = (JSONObject) userObject.get("region");
				if (regionObject != null) {
					Region region = new Region(regionObject);
					userProfileResponse.setRegion(region);
				} else {
					userProfileResponse.setRegion(null);
				}

			} catch (NullPointerException e) {
				throw new SystemException("Invalid response - null pointer exception");
			} catch (NumberFormatException e) {
				throw new SystemException("Invalid response - number format exception");
			} catch (ParseException e) {
				throw new SystemException("Invalid response - parse exception");
			}
		} else if (statusCode == 401) {
			throw new UnauthorizedException();
		} else {
			throw new SystemException("Invalid status code");
		}

		return userProfileResponse;
	}

}
