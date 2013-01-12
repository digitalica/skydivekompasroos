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
		CanopyType testType = getTestTypeByName("Stiletto");
		SpecificCanopy sc = new SpecificCanopy(1, testType.id, 170, TESTREMARK);
		sc.save(c);
		SpecificCanopy scReloaded = SpecificCanopy.getSpecificCanopy(c, 1);

		assertEquals(TESTREMARK, scReloaded.remarks);
		assertEquals(170, scReloaded.size);
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
