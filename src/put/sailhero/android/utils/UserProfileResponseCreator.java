package put.sailhero.android.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class UserProfileResponseCreator implements ResponseCreator<UserProfileResponse> {

	@Override
	public UserProfileResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException, UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		UserProfileResponse userProfileResponse = new UserProfileResponse();

		if (statusCode == 200) {
			try {
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONObject userObject = (JSONObject) obj.get("user");
				User user = new User();
				user.setId(Integer.valueOf(userObject.get("id").toString()));
				user.setEmail(userObject.get("email").toString());
				user.setName(userObject.get("name").toString());
				user.setSurname(userObject.get("surname").toString());
				user.setCreatedAt(userObject.get("created_at").toString());

				JSONObject yachtObject = (JSONObject) userObject.get("yacht");
				if (yachtObject != null) {
					Yacht yacht = new Yacht();
					yacht.setId(Integer.valueOf(yachtObject.get("id").toString()));
					yacht.setName(yachtObject.get("name").toString());
					yacht.setLength(Integer.valueOf(yachtObject.get("length").toString()));
					yacht.setWidth(Integer.valueOf(yachtObject.get("width").toString()));
					yacht.setCrew(Integer.valueOf(yachtObject.get("crew").toString()));

					userProfileResponse.setYacht(yacht);
				} else {
					userProfileResponse.setYacht(null);
				}

				JSONObject regionObject = (JSONObject) userObject.get("region");
				if (regionObject != null) {
					Region region = new Region();
					region.setId(Integer.valueOf(regionObject.get("id").toString()));
					region.setFullName(regionObject.get("full_name").toString());
					region.setCodeName(regionObject.get("code_name").toString());

					userProfileResponse.setRegion(region);
				} else {
					userProfileResponse.setRegion(null);
				}

				userProfileResponse.setUser(user);

			} catch (NullPointerException e) {
				throw new SystemException("Invalid response - null pointer exception");
			} catch (NumberFormatException e) {
				throw new SystemException("Invalid response - number format exception");
			} catch (ParseException e) {
				throw new SystemException("Invalid response - parse exception");
			}
		} else if (statusCode == 401) {
			throw new SystemException("Unauthorized");
		} else {
			throw new SystemException("Invalid status code");
		}

		return userProfileResponse;
	}

}
