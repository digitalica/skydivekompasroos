package nl.digitalica.skydivekompasroos.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.test.AndroidTestCase;

import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;
import nl.digitalica.skydivekompasroos.CanopyType;
import nl.digitalica.skydivekompasroos.SpecificCanopy;

public class SpecificCanopy_test extends AndroidTestCase {

	public void testAcceptablityForSpecificCanopies() {
		// Simulated PD 230
		assertEquals(AcceptabilityEnum.ACCEPTABLE,
				SpecificCanopy.acceptablility(1, 1, 230, 80));
		assertEquals(AcceptabilityEnum.ACCEPTABLE,
				SpecificCanopy.acceptablility(3, 1, 230, 80));
		assertEquals(AcceptabilityEnum.ACCEPTABLE,
				SpecificCanopy.acceptablility(3, 1, 230, 110));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				SpecificCanopy.acceptablility(3, 1, 230, 140));
		// simulated Stiletto 170
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				SpecificCanopy.acceptablility(1, 4, 170, 80));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				SpecificCanopy.acceptablility(3, 4, 170, 80));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				SpecificCanopy.acceptablility(4, 4, 170, 120));
		assertEquals(AcceptabilityEnum.ACCEPTABLE,
				SpecificCanopy.acceptablility(4, 4, 170, 110));
		assertEquals(AcceptabilityEnum.ACCEPTABLE,
				SpecificCanopy.acceptablility(6, 4, 170, 80));
	}

	public void testUniqueTypeNames() {
		List<CanopyType> canopyType = CanopyType
				.getAllCanopyTypesInList(getContext());
		HashMap<String, String> specificNamesSeen = new HashMap<String, String>();
		List<String> errors = new ArrayList<String>();
		for (CanopyType c : canopyType) {
			String s = c.specificName();
			if (specificNamesSeen.containsKey(s))
				errors.add("Duplicate specific name: " + s);
			specificNamesSeen.put(s, s);
		}
		assertTrue(errors.toString(), errors.size() == 0);
	}

	public void testSpecificCanopyReload() {
		final String TESTREMARK = "Test opmerking";
		Context c = getContext();
		SpecificCanopy.DeleteAll(c);
		CanopyType testType = getCanopyTypeByName("Stiletto");
		SpecificCanopy sc = new SpecificCanopy(1, testType.id, 170, TESTREMARK);
		sc.save(c);
		SpecificCanopy scReloaded = SpecificCanopy.getSpecificCanopy(c, 1);

		assertEquals(TESTREMARK, scReloaded.remarks);
		assertEquals(170, scReloaded.size);
	}

	public void testSavingMultipleSpecificCanopies() {
		final String TESTREMARK1 = "Test opmerking1";
		final String TESTREMARK2 = "Test opmerking2";
		final String TESTREMARK3 = "Test opmerking3";

		final int TESTSIZE1 = 170;
		final int TESTSIZE2 = 120;
		final int TESTSIZE3 = 130;

		Context c = getContext();
		SpecificCanopy.DeleteAll(c);
		SpecificCanopy sc;

		CanopyType t1 = getCanopyTypeByName("Stiletto");
		CanopyType t2 = getCanopyTypeByName("Katana");
		CanopyType t3 = getCanopyTypeByName("Crossfire");

		// save #1: Stiletto
		sc = new SpecificCanopy(1, t1.id, TESTSIZE1, TESTREMARK1);
		sc.save(c);
		// save #2: Katana
		sc = new SpecificCanopy(2, t2.id, TESTSIZE2, TESTREMARK2);
		sc.save(c);
		// save #3: CrossFire
		sc = new SpecificCanopy(3, t3.id, TESTSIZE3, TESTREMARK3);
		sc.save(c);

		// check we have 3 now
		assertEquals(3,SpecificCanopy.getSpecificCanopiesInList(c).size());

		// test #1: Stiletto
		sc = SpecificCanopy.getSpecificCanopy(c, 1);
		assertEquals(TESTSIZE1, sc.size);
		assertEquals(TESTREMARK1, sc.remarks);
		assertEquals(t1.id, sc.typeId);
		// test # 2: Katana
		sc = SpecificCanopy.getSpecificCanopy(c, 2);
		assertEquals(TESTSIZE2, sc.size);
		assertEquals(TESTREMARK2, sc.remarks);
		assertEquals(t2.id, sc.typeId);
		// test #3: Crossfire
		sc = SpecificCanopy.getSpecificCanopy(c, 3);
		assertEquals(TESTSIZE3, sc.size);
		assertEquals(TESTREMARK3, sc.remarks);
		assertEquals(t3.id, sc.typeId);

		// Now delete second one
		SpecificCanopy.delete(c, 2);
		
		// check we have 3 now
		assertEquals(2,SpecificCanopy.getSpecificCanopiesInList(c).size());
		
		// test old nr 3 is now nr 2.
		sc = SpecificCanopy.getSpecificCanopy(c, 2);
		assertEquals(TESTSIZE3, sc.size);
		assertEquals(TESTREMARK3, sc.remarks);
		assertEquals(t3.id, sc.typeId);
	}

	/**
	 * Returns a canopyType based on its name
	 * 
	 * @param name
	 * @return
	 */
	private CanopyType getCanopyTypeByName(String name) {
		// find Stiletto test canopy
		List<CanopyType> canopyTypes = CanopyType
				.getAllCanopyTypesInList(getContext());
		CanopyType testType = null;
		for (CanopyType ct : canopyTypes) {
			if (ct.name.equals(name))
				testType = ct;
		}
		assertFalse("testcanopy not found: " + name, testType == null);
		return testType;
	}

}
