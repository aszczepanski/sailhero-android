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
	protected void processOkStatusCode(HttpResponse response) throws InvalidResponseException {
		int status = response.getStatusCode();
		
		if (status == 200) {
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

	public static class Yacht {
		private Integer id;
	    private String name;
	    private Integer length;
	    private Integer width;
	    private Integer crew;
	    
		public Integer getId() {
			return id;
		}
		public void setId(Integer id) {
			this.id = id;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public Integer getLength() {
			return length;
		}
		public void setLength(Integer length) {
			this.length = length;
		}
		public Integer getWidth() {
			return width;
		}
		public void setWidth(Integer width) {
			this.width = width;
		}
		public Integer getCrew() {
			return crew;
		}
		public void setCrew(Integer crew) {
			this.crew = crew;
		}
	}
	
	public static class User {
		private Integer id;
		private String createdAt;  // TODO: change to datetime type
		private String email;
		private Integer yachId;
		
		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public String getCreatedAt() {
			return createdAt;
		}

		public void setCreatedAt(String createdAt) {
			this.createdAt = createdAt;
		}

		public String getEmail() {
			return email;
		}

		public void setEmail(String email) {
			this.email = email;
		}

		public Integer getYachId() {
			return yachId;
		}

		public void setYachId(Integer yachId) {
			this.yachId = yachId;
		}
		
		
	}
}
