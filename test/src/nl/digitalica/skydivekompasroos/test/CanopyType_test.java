package nl.digitalica.skydivekompasroos.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;
import nl.digitalica.skydivekompasroos.CanopyType;

public class CanopyType_test extends TestCase {
	final String CANOPYNAME1 = "testcanopy1";
	final String CANOPYNAME2 = "testcanopy2";

	/**
	 * This test is a little more extensive then may be expected, the unique
	 * name was used as a key in the past ;-)
	 */
	public void testUniqueName() {
		CanopyType testCanopy = new CanopyType(1, CANOPYNAME1);
		assertNotNull("Unique name should not be null", testCanopy.uniqueName());
		assertNotSame("Unique name should not be empty", "",
				testCanopy.uniqueName());
		assertNotSame("Unique name should not be equal to name", CANOPYNAME1,
				testCanopy.uniqueName());

		CanopyType testCanopy2 = new CanopyType(1, CANOPYNAME2);
		assertNotSame("Unique name should not be same on different canopy",
				testCanopy.uniqueName(), testCanopy2.uniqueName());
	}

	public void testComparatorByCategoryName() {
		List<CanopyType> testList = canopyListForSortTest();
		// compuComparator<Canopy> c = new Canopy.ComparatorByCategoryName();
		Collections.sort(testList, new CanopyType.ComparatorByCategoryName());
		assertSortingByCategoryName("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, new CanopyType.ComparatorByCategoryName());
		assertSortingByCategoryName("after reverse ", testList);
	}

	public void assertSortingByCategoryName(String runName,
			List<CanopyType> testList) {
		int max = testList.size() - 2; // skip special, and last in list
		for (int i = 0; i < max; i++) {
			CanopyType c1 = testList.get(i);
			CanopyType c2 = testList.get(i + 1);
			int c1cat = c1.calculationCategory();
			int c2cat = c2.calculationCategory();
			if (c1cat > c2cat)
				fail("Categories not sorted correctly: "
						+ Integer.toString(c1cat) + " / "
						+ Integer.toString(c2cat) + " in " + runName);
			if (c1cat == c2cat)
				if (c1.name.compareTo(c2.name) == 1)
					fail("Names not sorted correctly: " + c1.name + "  / "
							+ c2.name + " in " + runName);
		}
	}

	public void testComparatorByNameManufacturer() {
		List<CanopyType> testList = canopyListForSortTest();
		Comparator<CanopyType> c = new CanopyType.ComparatorByNameManufacturer();
		Collections.sort(testList, c);
		assertSortingByNameManufacturer("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, c);
		assertSortingByNameManufacturer("after reverse ", testList);
	}

	public void assertSortingByNameManufacturer(String runName,
			List<CanopyType> testList) {
		int max = testList.size() - 2;// skip special, and last in list
		for (int i = 0; i < max; i++) {
			CanopyType c1 = testList.get(i);
			CanopyType c2 = testList.get(i + 1);
			if (c1.name.compareTo(c2.name) == 1)
				fail("Names not sorted correctly: " + c1.name + "  / "
						+ c2.name + " in " + runName);
			if (c1.name.compareTo(c2.name) == 0) {
				if (c1.manufacturerName.compareTo(c2.manufacturerName) == 1)
					fail("Manufacturers not sorted correctly: "
							+ c1.manufacturerName + "  / "
							+ c2.manufacturerName + " in " + runName);
			}
		}
	}

	public void testComparatorByManufacturerCategoryName() {
		List<CanopyType> testList = canopyListForSortTest();
		Comparator<CanopyType> c = new CanopyType.ComparatorByManufacturerCategoryName();
		Collections.sort(testList, c);
		assertSortingByManufacturerCategoryName("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, c);
		assertSortingByManufacturerCategoryName("after reverse ", testList);
	}

	public void assertSortingByManufacturerCategoryName(String runName,
			List<CanopyType> testList) {
		int max = testList.size() - 2;// skip special, and last in list
		for (int i = 0; i < max; i++) {
			CanopyType c1 = testList.get(i);
			CanopyType c2 = testList.get(i + 1);
			if (c1.manufacturerName.compareTo(c2.manufacturerName) == 1)
				fail("Manufacturers not sorted correctly: "
						+ c1.manufacturerName + "  / " + c2.manufacturerName
						+ " in " + runName);
			int c1cat = c1.calculationCategory();
			int c2cat = c2.calculationCategory();
			if (c1.manufacturerName.compareTo(c2.manufacturerName) == 0)
				if (c1cat > c2cat)
					fail("Categories not sorted correctly: "
							+ Integer.toString(c1cat) + " / "
							+ Integer.toString(c2cat) + " in " + runName);
			if (c1.manufacturerName.compareTo(c2.manufacturerName) == 0
					&& c1cat == c2cat)
				if (c1.name.compareTo(c2.name) == 1)
					fail("Names not sorted correctly: " + c1.name + "  / "
							+ c2.name + " in " + runName);
		}
	}

	/***
	 * Create a list of 9 test canopies. In the list are 8 with different cat,
	 * name and manufacturer and one special canopy that should be sorted last.
	 * 
	 * @param manufacturer
	 * @return
	 */
	private List<CanopyType> canopyListForSortTest() {

		List<CanopyType> canopyList = canopyListForManufacturer("Manufacturer 1");
		// canopyList.addAll(canopyListForManufacturer("Manufacturer 2"));

		CanopyType testCanopyLast = new CanopyType(2, CANOPYNAME2);
		testCanopyLast.isSpecialCatchAllCanopy = true;
		canopyList.add(testCanopyLast);

		return canopyList;
	}

	/***
	 * Create a list of 4 test canopies with different names and cats
	 * 
	 * @param manufacturer
	 * @return
	 */
	private List<CanopyType> canopyListForManufacturer(String manufacturer) {
		CanopyType testCanopy11 = new CanopyType(1, CANOPYNAME1);
		testCanopy11.manufacturerName = manufacturer;
		CanopyType testCanopy12 = new CanopyType(1, CANOPYNAME2);
		testCanopy12.manufacturerName = manufacturer;
		CanopyType testCanopy21 = new CanopyType(3, CANOPYNAME1);
		testCanopy21.manufacturerName = manufacturer;
		CanopyType testCanopy22 = new CanopyType(3, CANOPYNAME2);
		testCanopy22.manufacturerName = manufacturer;

		List<CanopyType> canopyList = new ArrayList<CanopyType>();
		canopyList.add(testCanopy11);
		canopyList.add(testCanopy12);
		canopyList.add(testCanopy21);
		canopyList.add(testCanopy22);

		return canopyList;
	}

	public void testAcceptablity() {
		CanopyType beginnerCanopy = new CanopyType(1, "beginnercanopy", "230");
		CanopyType intermediateCanopy120 = new CanopyType(3,
				"intermediatecanopy120", "120");
		CanopyType intermediateCanopy190 = new CanopyType(3,
				"intermediatecanopy190", "190");
		CanopyType expertCanopy = new CanopyType(6, "expertcanopy", "90");

		// beginner can jump beginner canopy
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 1, 50, beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 1, 100,
				beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE, 1, 120,
				beginnerCanopy);

		// beginner can NOT EVER jump intermediate canopy
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 50,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 100,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 120,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 50,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 100,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 120,
				intermediateCanopy190);

		// beginner can NOT EVER jump expert canopy
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 50,
				expertCanopy);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 100,
				expertCanopy);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 1, 120,
				expertCanopy);

		// intermediate can jump beginner canopy
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 3, 50, beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 3, 100,
				beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 3, 120,
				beginnerCanopy);

		// intermediate can jump intermediate canopy IF wingload is ok
		assertAcceptability(AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE, 3, 50,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE, 3, 100,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE, 3, 120,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 3, 50,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 3, 100,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE, 3, 120,
				intermediateCanopy190);

		// intermediate can NOT jump expert canopy
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 3, 50,
				expertCanopy);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 3, 100,
				expertCanopy);
		assertAcceptability(AcceptabilityEnum.CATEGORYTOOHIGH, 3, 120,
				expertCanopy);

		// expert can always jump beginner canopy
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 50, beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 100,
				beginnerCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 120,
				beginnerCanopy);

		// expert can always jump intermediate canopy
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 50,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 100,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 120,
				intermediateCanopy120);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 50,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 100,
				intermediateCanopy190);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 120,
				intermediateCanopy190);

		// expert can always jump expert canopy
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 50, expertCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 100, expertCanopy);
		assertAcceptability(AcceptabilityEnum.ACCEPTABLE, 6, 120, expertCanopy);
	}

	private void assertAcceptability(AcceptabilityEnum acceptability,
			int jumperCategory, int exitWeightInKg, CanopyType canopy) {
		AcceptabilityEnum canopyAcceptability = canopy.acceptablility(
				jumperCategory, exitWeightInKg);
		StringBuilder message = new StringBuilder();
		message.append("Jumper Cat " + Integer.toString(jumperCategory));
		message.append(", exit weight " + Integer.toString(exitWeightInKg)
				+ " kg");
		message.append(", " + canopy.name);
		message.append(" cat " + Integer.toString(canopy.calculationCategory()));
		message.append(", area " + canopy.minSize + "/" + canopy.maxSize);
		assertEquals(message.toString(), acceptability, canopyAcceptability);
	}

	public void testAddionalInformation() {
		CanopyType c = new CanopyType(1, "testCanopy");
		assertTrue("Empty canopy needs more info",
				c.addtionalInformationNeeded());
		c.cells = "9";
		assertTrue("Canopy with only cells needs more info",
				c.addtionalInformationNeeded());
		c.minSize = "10";
		assertTrue("Canopy with cells, minsize needs more info",
				c.addtionalInformationNeeded());
		c.maxSize = "20";
		assertTrue("Canopy with cells, minsize, maxsize needs more info",
				c.addtionalInformationNeeded());
		c.firstYearOfProduction = "2010";
		assertFalse(
				"Canopy with cells, minsize, maxsize and first year of production needs NO more info",
				c.addtionalInformationNeeded());
	}

}
