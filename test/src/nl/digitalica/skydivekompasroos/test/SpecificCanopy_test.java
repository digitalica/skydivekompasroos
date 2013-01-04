package nl.digitalica.skydivekompasroos.test;

import java.util.UUID;

import junit.framework.TestCase;
import nl.digitalica.skydivekompasroos.Canopy;
import nl.digitalica.skydivekompasroos.SpecificCanopy;


public class SpecificCanopy_test  extends TestCase {
	
	
	public void testAcceptablity() {
		final String TESTREMARK = "Test";
		
		UUID fakeTypeId = UUID.randomUUID();
		SpecificCanopy sc;
		// Simulated PD 230 
		sc = new SpecificCanopy(fakeTypeId, 230, 1, TESTREMARK);
		assertEquals(Canopy.ACCEPTABLE,sc.acceptablility(1, 80));
		assertEquals(Canopy.ACCEPTABLE,sc.acceptablility(3, 80));
		assertEquals(Canopy.ACCEPTABLE,sc.acceptablility(3, 110));
		assertEquals(Canopy.NEEDEDSIZENOTAVAILABLE,sc.acceptablility(3, 140));
		// simulated Stiletto 170
		sc = new SpecificCanopy(fakeTypeId, 170, 4, TESTREMARK);
		assertEquals(Canopy.CATEGORYTOOHIGH,sc.acceptablility(1, 80));
		assertEquals(Canopy.CATEGORYTOOHIGH,sc.acceptablility(3, 80));
		assertEquals(Canopy.NEEDEDSIZENOTAVAILABLE,sc.acceptablility(4, 120));
		assertEquals(Canopy.ACCEPTABLE,sc.acceptablility(4, 110));
		assertEquals(Canopy.ACCEPTABLE,sc.acceptablility(6, 80));

	}


}
