package put.sailhero.android.util;

import put.sailhero.android.util.model.User;


public class CreateUserResponse implements ProcessedResponse {
	
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
