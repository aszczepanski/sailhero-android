package put.sailhero.test.util.model;

import org.json.simple.JSONObject;
import org.junit.Test;

import put.sailhero.model.Port;
import android.location.Location;
import android.test.AndroidTestCase;

public class PortTest extends AndroidTestCase {

	private Port port;

	private final static double eps = 0.0001;

	@Override
	protected void setUp() throws Exception {
		port = null;
		super.setUp();
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testPortConstructorFromJSONObject() {
		JSONObject portObject = new JSONObject();

		portObject.put("id", 1);
		portObject.put("name", "Stranda");
		portObject.put("latitude", 54.043302);
		portObject.put("longitude", 21.738819);
		portObject.put("city", "Gizycko");
		portObject.put("street", "Wojska Polskiego 7");
		portObject.put("telephone", "+48 501 122 610");
		portObject.put("website", "http://stranda.pl/");
		portObject.put("additional_info", "Showers for 6 minutes");
		portObject.put("spots", 70);
		portObject.put("depth", 5);
		portObject.put("price_per_person", 10.0);
		portObject.put("price_power_connection", 10.0);
		portObject.put("price_wc", 0.0);
		portObject.put("price_shower", 12.0);
		portObject.put("price_washbasin", 2.0);
		portObject.put("price_dishes", 0.0);
		portObject.put("price_wifi", 0.0);
		portObject.put("price_washing_machine", 15.0);
		portObject.put("price_emptying_chemical_toilet", 0.0);
		portObject.put("price_parking", 0.0);
		portObject.put("has_power_connection", true);
		portObject.put("has_wc", true);
		portObject.put("has_shower", true);
		portObject.put("has_washbasin", true);
		portObject.put("has_dishes", true);
		portObject.put("has_wifi", true);
		portObject.put("has_parking", true);
		portObject.put("has_slip", false);
		portObject.put("has_washing_machine", true);
		portObject.put("has_fuel_station", false);
		portObject.put("has_emptying_chemical_toilet", true);

		port = new Port(portObject);

		assertNotNull(port);
		assertEquals((int) port.getId(), 1);
		assertEquals(port.getName(), "Stranda");

		Location location = port.getLocation();
		assertNotNull(location);
		assertEquals(location.getLatitude(), 54.043302, eps);
		assertEquals(location.getLongitude(), 21.738819, eps);

		assertEquals(port.getCity(), "Gizycko");
		assertEquals(port.getStreet(), "Wojska Polskiego 7");
		assertEquals(port.getTelephone(), "+48 501 122 610");
		assertEquals(port.getWebsite(), "http://stranda.pl/");
		assertEquals(port.getAdditionalInfo(), "Showers for 6 minutes");
		
		assertEquals((int) port.getSpots(), 70);
		assertEquals((int) port.getDepth(), 5);
		
		assertEquals(port.getPricePerPerson(), 10.0, eps);
		assertEquals(port.getPricePowerConnection(), 10.0, eps);
		assertEquals(port.getPriceWC(), 0.0, eps);
		assertEquals(port.getPriceShower(), 12.0, eps);
		assertEquals(port.getPriceWashbasin(), 2.0, eps);
		assertEquals(port.getPriceDishes(), 0.0, eps);
		assertEquals(port.getPriceWifi(), 0.0, eps);
		assertEquals(port.getPriceWashingMachine(), 15.0, eps);
		assertEquals(port.getPriceEmptyingChemicalToilet(), 0.0, eps);
		assertEquals(port.getPriceParking(), 0.0, eps);
		
		assertEquals(port.isHasPowerConnection(), true);
		assertEquals(port.isHasWC(), true);
		assertEquals(port.isHasShower(), true);
		assertEquals(port.isHasWashbasin(), true);
		assertEquals(port.isHasDishes(), true);
		assertEquals(port.isHasWifi(), true);
		assertEquals(port.isHasParking(), true);
		assertEquals(port.isHasSlip(), false);
		assertEquals(port.isHasWashingMachine(), true);
		assertEquals(port.isHasFuelStation(), false);
		assertEquals(port.isHasEmptyingChemicalToilet(), true);
	}
}
