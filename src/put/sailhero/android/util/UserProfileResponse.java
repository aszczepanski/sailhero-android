package put.sailhero.android.util;

import put.sailhero.android.util.model.Region;
import put.sailhero.android.util.model.User;
import put.sailhero.android.util.model.Yacht;

public class UserProfileResponse implements ProcessedResponse {

	private User user;
	private Yacht yacht;
	private Region region;

	public Yacht getYacht() {
		return yacht;
	}

	public void setYacht(Yacht yacht) {
		this.yacht = yacht;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}
}
