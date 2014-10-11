package put.sailhero.android.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.SystemException;
import put.sailhero.android.exception.UnprocessableEntityException;

public class CreateUserResponseCreator implements ResponseCreator<CreateUserResponse> {

	@Override
	public CreateUserResponse createFrom(HttpResponse response) throws SystemException, UnprocessableEntityException {
		CreateUserResponse createUserResponse = new CreateUserResponse();

		int statusCode = response.getStatusCode();

		if (statusCode == 201) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());

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
				
				createUserResponse.setUser(user);

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
		
		return createUserResponse;
	}

}
