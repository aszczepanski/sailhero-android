package put.sailhero.android;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResponseException;

public class CreateUserResponse extends ProcessedResponse {
	
	private User user;

	@Override
	protected void processOkStatusCode(HttpResponse response)
			throws InvalidResponseException {
		int status = response.getStatusCode();
		
		if (status == 201) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());
				
				JSONObject userObject = (JSONObject) obj.get("user");
				user = new User();
				user.setId(Integer.valueOf(userObject.get("id").toString()));
				user.setEmail(userObject.get("email").toString());
				user.setCreatedAt(userObject.get("created_at").toString());
				if (userObject.get("yacht_id") == null) {
					user.setYachId(null);
				} else {
					user.setYachId(Integer.valueOf(userObject.get("yacht_id").toString()));
				}

			} catch (NullPointerException e) {
				throw new InvalidResponseException(e.getMessage());
			} catch (NumberFormatException e) {
				throw new InvalidResponseException(e.getMessage());
			} catch (ParseException e) {
				throw new InvalidResponseException(e.getMessage());
			}
		} else {
			throw new InvalidResponseException("Invalid status code");
		}
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
