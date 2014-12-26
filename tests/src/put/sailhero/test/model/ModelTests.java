package put.sailhero.test.model;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ModelTests extends TestCase {

	public static Test suite() {
		TestSuite suite = new TestSuite(ModelTests.class.getName());

		suite.addTestSuite(AlertTest.class);
		suite.addTestSuite(PortTest.class);
		suite.addTestSuite(RegionTest.class);
		suite.addTestSuite(UserTest.class);
		suite.addTestSuite(YachtTest.class);

		return suite;
	}

}
