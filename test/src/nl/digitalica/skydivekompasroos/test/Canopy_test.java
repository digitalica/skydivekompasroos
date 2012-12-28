package nl.digitalica.skydivekompasroos.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import junit.framework.TestCase;
import nl.digitalica.skydivekompasroos.Canopy;

public class Canopy_test extends TestCase {
	final String CANOPYNAME1 = "testcanopy1";
	final String CANOPYNAME2 = "testcanopy2";

	public void testKey() {

		Canopy testCanopy = new Canopy(1, CANOPYNAME1);
		assertNotNull("Key should not be null", testCanopy.key());
		assertNotSame("Key should not be empty", "", testCanopy.key());
		assertNotSame("Key should not be equal to name", CANOPYNAME1,
				testCanopy.key());
		assertNotSame("Key should not be equal to manufacturer",
				Canopy.DEFAULTMANUFACTURER, testCanopy.key());

		Canopy testCanopy2 = new Canopy(1, CANOPYNAME2);
		assertNotSame("Key should not be same on different canopy",
				testCanopy.key(), testCanopy2.key());
	}

	public void testComparatorByCategoryName() {
		List<Canopy> testList = canopyListForSortTest();
		// compuComparator<Canopy> c = new Canopy.ComparatorByCategoryName();
		Collections.sort(testList, new Canopy.ComparatorByCategoryName());
		assertSortingByCategoryName("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, new Canopy.ComparatorByCategoryName());
		assertSortingByCategoryName("after reverse ", testList);
	}

	public void assertSortingByCategoryName(String runName,
			List<Canopy> testList) {
		int max = testList.size() - 2; // skip special, and last in list
		for (int i = 0; i < max; i++) {
			Canopy c1 = testList.get(i);
			Canopy c2 = testList.get(i + 1);
			if (c1.category > c2.category)
				fail("Categories not sorted correctly: "
						+ Integer.toString(c1.category) + " / "
						+ Integer.toString(c2.category) + " in " + runName);
			if (c1.category == c2.category)
				if (c1.name.compareTo(c2.name) == 1)
					fail("Names not sorted correctly: " + c1.name + "  / "
							+ c2.name + " in " + runName);
		}
	}

	public void testComparatorByNameManufacturer() {
		List<Canopy> testList = canopyListForSortTest();
		Comparator<Canopy> c = new Canopy.ComparatorByNameManufacturer();
		Collections.sort(testList, c);
		assertSortingByNameManufacturer("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, c);
		assertSortingByNameManufacturer("after reverse ", testList);
	}

	public void assertSortingByNameManufacturer(String runName,
			List<Canopy> testList) {
		int max = testList.size() - 2;// skip special, and last in list
		for (int i = 0; i < max; i++) {
			Canopy c1 = testList.get(i);
			Canopy c2 = testList.get(i + 1);
			if (c1.name.compareTo(c2.name) == 1)
				fail("Names not sorted correctly: " + c1.name + "  / "
						+ c2.name + " in " + runName);
			if (c1.name.compareTo(c2.name) == 0)
				if (c1.manufacturer.compareTo(c2.manufacturer) == 1)
					fail("Manufacturers not sorted correctly: "
							+ c1.manufacturer + "  / " + c2.manufacturer
							+ " in " + runName);
		}
	}

	public void testComparatorByManufacturerCategoryName() {
		List<Canopy> testList = canopyListForSortTest();
		Comparator<Canopy> c = new Canopy.ComparatorByManufacturerCategoryName();
		Collections.sort(testList, c);
		assertSortingByManufacturerCategoryName("first run ", testList);
		Collections.reverse(testList);
		Collections.sort(testList, c);
		assertSortingByManufacturerCategoryName("after reverse ", testList);
	}

	public void assertSortingByManufacturerCategoryName(String runName,
			List<Canopy> testList) {
		int max = testList.size() - 2;// skip special, and last in list
		for (int i = 0; i < max; i++) {
			Canopy c1 = testList.get(i);
			Canopy c2 = testList.get(i + 1);
			if (c1.manufacturer.compareTo(c2.manufacturer) == 1)
				fail("Manufacturers not sorted correctly: " + c1.manufacturer
						+ "  / " + c2.manufacturer + " in " + runName);
			if (c1.manufacturer.compareTo(c2.manufacturer) == 0)
				if (c1.category > c2.category)
					fail("Categories not sorted correctly: "
							+ Integer.toString(c1.category) + " / "
							+ Integer.toString(c2.category) + " in " + runName);
			if (c1.manufacturer.compareTo(c2.manufacturer) == 0
					&& c1.category == c2.category)
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
	private List<Canopy> canopyListForSortTest() {

		List<Canopy> canopyList = canopyListForManufacturer("Manufacturer 1");
		// canopyList.addAll(canopyListForManufacturer("Manufacturer 2"));

		Canopy testCanopyLast = new Canopy(2, CANOPYNAME2);
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
	private List<Canopy> canopyListForManufacturer(String manufacturer) {
		Canopy testCanopy11 = new Canopy(1, CANOPYNAME1);
		testCanopy11.manufacturer = manufacturer;
		Canopy testCanopy12 = new Canopy(1, CANOPYNAME2);
		testCanopy12.manufacturer = manufacturer;
		Canopy testCanopy21 = new Canopy(3, CANOPYNAME1);
		testCanopy21.manufacturer = manufacturer;
		Canopy testCanopy22 = new Canopy(3, CANOPYNAME2);
		testCanopy22.manufacturer = manufacturer;

		List<Canopy> canopyList = new ArrayList<Canopy>();
		canopyList.add(testCanopy11);
		canopyList.add(testCanopy12);
		canopyList.add(testCanopy21);
		canopyList.add(testCanopy22);

		return canopyList;

	}

	public void testAcceptablity() {
		Canopy beginnerCanopy = new Canopy(1, "beginnercanopy", "230");
		Canopy intermediateCanopy120 = new Canopy(3, "intermediatecanopy120",
				"120");
		Canopy intermediateCanopy190 = new Canopy(3, "intermediatecanopy190",
				"190");
		Canopy expertCanopy = new Canopy(6, "expertcanopy", "90");

		// beginner can jump beginner canopy
		assertAcceptability(Canopy.ACCEPTABLE, 1, 50, beginnerCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 1, 100, beginnerCanopy);
		assertAcceptability(Canopy.NEEDEDSIZENOTAVAILABLE, 1, 120,
				beginnerCanopy);

		// beginner can NOT EVER jump intermediate canopy
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 50,
				intermediateCanopy120);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 100,
				intermediateCanopy120);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 120,
				intermediateCanopy120);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 50,
				intermediateCanopy190);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 100,
				intermediateCanopy190);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 120,
				intermediateCanopy190);

		// beginner can NOT EVER jump expert canopy
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 50, expertCanopy);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 100, expertCanopy);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 1, 120, expertCanopy);

		// intermediate can jump beginner canopy
		assertAcceptability(Canopy.ACCEPTABLE, 3, 50, beginnerCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 3, 100, beginnerCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 3, 120, beginnerCanopy);

		// intermediate can jump intermediate canopy IF wingload is ok
		assertAcceptability(Canopy.NEEDEDSIZENOTAVAILABLE, 3, 50,
				intermediateCanopy120);
		assertAcceptability(Canopy.NEEDEDSIZENOTAVAILABLE, 3, 100,
				intermediateCanopy120);
		assertAcceptability(Canopy.NEEDEDSIZENOTAVAILABLE, 3, 120,
				intermediateCanopy120);
		assertAcceptability(Canopy.ACCEPTABLE, 3, 50, intermediateCanopy190);
		assertAcceptability(Canopy.ACCEPTABLE, 3, 100, intermediateCanopy190);
		assertAcceptability(Canopy.NEEDEDSIZENOTAVAILABLE, 3, 120,
				intermediateCanopy190);

		// intermediate can NOT jump expert canopy
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 3, 50, expertCanopy);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 3, 100, expertCanopy);
		assertAcceptability(Canopy.CATEGORYTOOHIGH, 3, 120, expertCanopy);

		// expert can always jump beginner canopy
		assertAcceptability(Canopy.ACCEPTABLE, 6, 50, beginnerCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 100, beginnerCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 120, beginnerCanopy);

		// expert can always jump intermediate canopy
		assertAcceptability(Canopy.ACCEPTABLE, 6, 50, intermediateCanopy120);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 100, intermediateCanopy120);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 120, intermediateCanopy120);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 50, intermediateCanopy190);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 100, intermediateCanopy190);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 120, intermediateCanopy190);

		// expert can always jump expert canopy
		assertAcceptability(Canopy.ACCEPTABLE, 6, 50, expertCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 100, expertCanopy);
		assertAcceptability(Canopy.ACCEPTABLE, 6, 120, expertCanopy);
	}

	private void assertAcceptability(int acceptability, int jumperCategory,
			int exitWeightInKg, Canopy canopy) {
		int acceptablity = canopy
				.acceptablility(jumperCategory, exitWeightInKg);
		StringBuilder message = new StringBuilder();
		message.append("Jumper Cat " + Integer.toString(jumperCategory));
		message.append(", exit weight " + Integer.toString(exitWeightInKg)
				+ " kg");
		message.append(", " + canopy.name);
		message.append(" cat " + Integer.toString(canopy.category));
		message.append(", area " + canopy.minSize + "/" + canopy.maxSize);
		assertEquals(message.toString(), acceptability, acceptablity);
	}

	public void testAddionalInformation() {
		Canopy c = new Canopy(1, "testCanopy");
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
