package put.sailhero.test.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import put.sailhero.model.User;
import android.test.AndroidTestCase;

public class UserTest extends AndroidTestCase {

	private User user;
	
	@SuppressWarnings("unchecked")
	@Test
	public void testUserConstructorFromJSONObject() {
		JSONObject userObject = new JSONObject();

		userObject.put("id", 1);
		userObject.put("name", "Adam");
		userObject.put("surname", "Szczepanski");
		userObject.put("email", "example@gmail.com");

		user = new User(userObject);

		assertNotNull(user);
		assertEquals((int) user.getId(), 1);
		assertEquals(user.getName(), "Adam");
		assertEquals(user.getSurname(), "Szczepanski");
		assertEquals(user.getEmail(), "example@gmail.com");
	}

}
