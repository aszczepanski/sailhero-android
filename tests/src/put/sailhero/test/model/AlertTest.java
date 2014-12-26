package put.sailhero.test.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import put.sailhero.model.Alert;
import android.test.AndroidTestCase;

public class AlertTest extends AndroidTestCase {

	private final static double eps = 0.0001;

	private Alert alert;

	@Override
	protected void setUp() throws Exception {
		alert = null;
		super.setUp();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAlertConstructorFromJSONObject() {
		JSONObject alertObject = new JSONObject();

		alertObject.put("id", 1);
		alertObject.put("alert_type", "CLOSED_AREA");
		alertObject.put("additional_info", "");
		alertObject.put("credibility", 1);
		alertObject.put("latitude", 52.13);
		alertObject.put("longitude", 47.75);
		alertObject.put("user_id", 1);

		alert = new Alert(alertObject);

		assertNotNull(alert);
		assertEquals((int) alert.getId(), 1);
		assertEquals("CLOSED_AREA", alert.getAlertType());
		assertEquals("", alert.getAdditionalInfo());
		assertEquals((int) alert.getCredibility(), 1);
		assertEquals((double) alert.getLocation().getLatitude(), 52.13, eps);
		assertEquals((double) alert.getLocation().getLongitude(), 47.75, eps);
		assertEquals((int) alert.getUserId(), 1);
	}

	@Test
	public void testToJSONObject() {
		alert = new Alert();
		alert.setId(1);
		alert.setAlertType("YACHT_FAILURE");
		alert.setCredibility(-13);
		alert.setAdditionalInfo("info");
		alert.setLatitude(52.13);
		alert.setLongitude(47.75);

		JSONObject alertObject = alert.toJSONObject();

		assertNotNull(alertObject);
		assertNull(alertObject.get("id"));
		assertEquals(alertObject.get("alert_type"), "YACHT_FAILURE");
		assertEquals(alertObject.get("additional_info"), "info");
		assertNull(alertObject.get("credibility"));
		assertEquals((double) alertObject.get("latitude"), 52.13, eps);
		assertEquals((double) alertObject.get("longitude"), 47.75, eps);
	}
}
