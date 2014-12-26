package put.sailhero.test.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import put.sailhero.model.Yacht;
import android.test.AndroidTestCase;

public class YachtTest extends AndroidTestCase {

	private Yacht yacht;

	@Override
	protected void setUp() throws Exception {
		yacht = null;
		super.setUp();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testYachtConstructorFromJSONObject() {
		JSONObject yachtObject = new JSONObject();

		yachtObject.put("id", 1);
		yachtObject.put("name", "Tango");
		yachtObject.put("length", 780);
		yachtObject.put("width", 315);
		yachtObject.put("crew", 7);

		yacht = new Yacht(yachtObject);

		assertNotNull(yacht);
		assertEquals((int) yacht.getId(), 1);
		assertEquals(yacht.getName(), "Tango");
		assertEquals((int) yacht.getLength(), 780);
		assertEquals((int) yacht.getWidth(), 315);
		assertEquals((int) yacht.getCrew(), 7);
	}

	@Test
	public void testToJSONObject() {
		yacht = new Yacht();
		yacht.setId(1);
		yacht.setName("Tango");
		yacht.setLength(780);
		yacht.setWidth(315);
		yacht.setCrew(7);

		JSONObject yachtObject = yacht.toJSONObject();

		assertNotNull(yachtObject);
		assertNull(yachtObject.get("id"));
		assertEquals(yachtObject.get("name"), "Tango");
		assertEquals((int) yachtObject.get("length"), 780);
		assertEquals((int) yachtObject.get("width"), 315);
		assertEquals((int) yachtObject.get("crew"), 7);
	}
}
