package put.sailhero.test.util;

import org.junit.Test;

import put.sailhero.Config;
import put.sailhero.util.UnitUtils;
import android.test.AndroidTestCase;
import android.util.Log;

public class UnitUtilsTest extends AndroidTestCase {

	final private float EPS = 0.0001f;

	@Test
	public void testRoundDistanceTo25() {
		assertEquals((int) UnitUtils.roundDistanceTo25(112.49f), 100);
		assertEquals((int) UnitUtils.roundDistanceTo25(112.5f), 125);
		assertEquals((int) UnitUtils.roundDistanceTo25(25.f), 25);
	}

	@Test
	public void testConvertMetresToKnots() {
		assertEquals((float) UnitUtils.convertMetresToKnots(2.f), 2.f * 1.943844f, EPS);
	}

	@Test
	public void testRoundSpeedToHalf() {
		assertEquals((float) UnitUtils.roundSpeedToHalf(0.f), 0.f, EPS);
		assertEquals((float) UnitUtils.roundSpeedToHalf(0.5f), 0.5f, EPS);
		assertEquals((float) UnitUtils.roundSpeedToHalf(11.24f), 11.f, EPS);
		assertEquals((float) UnitUtils.roundSpeedToHalf(11.25f), 11.5f, EPS);
	}

	@Test
	public void testDecimalToDegMinSec() {
		UnitUtils.DegMinSec degMinSec = null;

		degMinSec = UnitUtils.decimalToDegMinSec(50.0);
		assertEquals((int) degMinSec.getDegrees(), 50);
		assertEquals((int) degMinSec.getMinutes(), 0);
		assertEquals((int) degMinSec.getSeconds(), 0);
		assertEquals((int) degMinSec.getRest(), 0);

		degMinSec = UnitUtils.decimalToDegMinSec(25.05823);
		assertEquals((int) degMinSec.getDegrees(), 25);
		assertEquals((int) degMinSec.getMinutes(), 3);
		assertEquals((int) degMinSec.getSeconds(), 29);
		assertEquals((int) degMinSec.getRest(), 628);

		degMinSec = UnitUtils.decimalToDegMinSec(49.9999);
		assertEquals((int) degMinSec.getDegrees(), 49);
		assertEquals((int) degMinSec.getMinutes(), 59);
		assertEquals((int) degMinSec.getSeconds(), 59);
		assertEquals((int) degMinSec.getRest(), 640);

		// TODO: test negative values handling
	}

}
