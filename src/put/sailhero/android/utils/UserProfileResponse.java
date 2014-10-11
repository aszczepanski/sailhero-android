package put.sailhero.android.utils;

import java.util.LinkedList;

public class UserProfileResponse implements ProcessedResponse {
	
	private LinkedList<Yacht> yachts;
	private User user;

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
