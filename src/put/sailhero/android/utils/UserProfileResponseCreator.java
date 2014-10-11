package put.sailhero.android.utils;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResourceOwnerException;
import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class UserProfileResponseCreator implements ResponseCreator<UserProfileResponse> {

	@Override
	public UserProfileResponse createFrom(HttpResponse response)
			throws InvalidResourceOwnerException, SystemException,
			UnprocessableEntityException {
		int statusCode = response.getStatusCode();

		UserProfileResponse userProfileResponse = new UserProfileResponse();
		
		if (statusCode == 200) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

				JSONArray yachtsArray = (JSONArray) obj.get("yachts");
				LinkedList<Yacht> yachts = new LinkedList<Yacht>();
				for (int i=0; i<yachtsArray.size(); i++) {
					JSONObject yachtObject = (JSONObject) yachtsArray.get(i);
					Yacht yacht = new Yacht();
					yacht.setId(Integer.valueOf(yachtObject.get("id").toString()));
					yacht.setName(yachtObject.get("name").toString());
					yacht.setLength(Integer.valueOf(yachtObject.get("length").toString()));
					yacht.setWidth(Integer.valueOf(yachtObject.get("width").toString()));
					yacht.setCrew(Integer.valueOf(yachtObject.get("crew").toString()));
					yachts.add(yacht);
				}

				userProfileResponse.setYachts(yachts);
				
				JSONObject userObject = (JSONObject) obj.get("user");
				User user = new User();
				user.setId(Integer.valueOf(userObject.get("id").toString()));
				user.setEmail(userObject.get("email").toString());
				user.setCreatedAt(userObject.get("created_at").toString());
				if (userObject.get("yacht_id") == null) {
					user.setYachId(null);
				} else {
					user.setYachId(Integer.valueOf(userObject.get("yacht_id").toString()));
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
