package put.sailhero.test.util.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import put.sailhero.model.Region;
import android.test.AndroidTestCase;

public class RegionTest extends AndroidTestCase {

	private Region region;

	@SuppressWarnings("unchecked")
	@Test
	public void testRegionConstructorFromJSONObject() {
		JSONObject regionObject = new JSONObject();

		regionObject.put("id", 1);
		regionObject.put("full_name", "Wielkie Jeziora Mazurskie");
		regionObject.put("code_name", "MAZURY");

		region = new Region(regionObject);

		assertNotNull(region);
		assertEquals((int) region.getId(), 1);
		assertEquals(region.getFullName(), "Wielkie Jeziora Mazurskie");
		assertEquals(region.getCodeName(), "MAZURY");
	}

}
