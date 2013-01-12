package nl.digitalica.skydivekompasroos.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.test.AndroidTestCase;

import junit.framework.TestCase;
import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;
import nl.digitalica.skydivekompasroos.CanopyType;
import nl.digitalica.skydivekompasroos.SpecificCanopy;

public class SpecificCanopy_test extends AndroidTestCase {

	public void testAcceptablityForSpecificCanopies() {
		final String TESTNAME = "TestName";
		final String TESTREMARK = "TestRemark";
		UUID fakeTypeId = UUID.randomUUID();
		SpecificCanopy sc;
		// Simulated PD 230
		sc = new SpecificCanopy(1, fakeTypeId, 230, TESTNAME, 1, TESTREMARK);
		assertEquals(AcceptabilityEnum.ACCEPTABLE, sc.acceptablility(1, 80));
		assertEquals(AcceptabilityEnum.ACCEPTABLE, sc.acceptablility(3, 80));
		assertEquals(AcceptabilityEnum.ACCEPTABLE, sc.acceptablility(3, 110));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				sc.acceptablility(3, 140));
		// simulated Stiletto 170
		sc = new SpecificCanopy(2, fakeTypeId, 170, TESTNAME, 4, TESTREMARK);
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				sc.acceptablility(1, 80));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				sc.acceptablility(3, 80));
		assertEquals(AcceptabilityEnum.CATEGORYTOOHIGH,
				sc.acceptablility(4, 120));
		assertEquals(AcceptabilityEnum.ACCEPTABLE, sc.acceptablility(4, 110));
		assertEquals(AcceptabilityEnum.ACCEPTABLE, sc.acceptablility(6, 80));

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
		CanopyType testType = getTestTypeByName("Stiletto");
		SpecificCanopy sc = new SpecificCanopy(1, testType.id, 170,
				testType.name, 4, TESTREMARK);
		sc.save(c);
		SpecificCanopy scReloaded = SpecificCanopy.getSpecificCanopy(c, 1);

		assertEquals(TESTREMARK, scReloaded.remarks);
		assertEquals(170, scReloaded.size);
		assertEquals(testType.name, scReloaded.typeName);
		assertEquals(testType.category, scReloaded.typeCategory);
	}

	private CanopyType getTestTypeByName(String name) {
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
