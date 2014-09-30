package put.sailhero.android;

import java.util.LinkedList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import put.sailhero.android.exception.InvalidResponseException;

public class UserProfileResponse extends ProcessedResponse {
	
	private LinkedList<Yacht> yachts;
	private User user;

	@Override
	public void createFrom(HttpResponse response) throws InvalidResponseException {
		int statusCode = response.getStatusCode();
		
		if (statusCode == 200) {
			try {			
				JSONParser parser = new JSONParser();
				JSONObject obj = (JSONObject) parser.parse(response.getBody());
				
				JSONArray yachtsArray = (JSONArray) obj.get("yachts");
				yachts = new LinkedList<Yacht>();
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

	public LinkedList<Yacht> getYachts() {
		return yachts;
	}

	public void setYachts(LinkedList<Yacht> yachts) {
		this.yachts = yachts;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
}
