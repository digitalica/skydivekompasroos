package nl.digitalica.skydivekompasroos.test;

import nl.digitalica.skydivekompasroos.Calculation;
import junit.framework.TestCase;

public class Calculation_test extends TestCase {

	final static int MINAREACAT1 = 170;
	final static int MINAREACAT2 = 170;
	final static int MINAREACAT3 = 150;
	final static int MINAREACAT4 = 135;
	final static int MINAREACAT5 = 120;

	final static double MAXWINGLOADCAT1 = 1.1;
	final static double MAXWINGLOADCAT2 = 1.1;
	final static double MAXWINGLOADCAT3 = 1.3;
	final static double MAXWINGLOADCAT4 = 1.5;
	final static double MAXWINGLOADCAT5 = 1.7;

	public void testKgToLbs() {
		assertEquals(0, Calculation.kgToLbs(0));
		assertEquals(4, Calculation.kgToLbs(2));
		assertEquals(441, Calculation.kgToLbs(200));
		assertEquals(443, Calculation.kgToLbs(201));
	}

	public void testJumperCategory() {
		assertEquals(1, Calculation.jumperCategory(0, 0)); // absolute beginner
		assertEquals(6, Calculation.jumperCategory(2000, 1000)); // sky god
	}

	public void testMinAreaBasedOnCagetory() {
		assertEquals(MINAREACAT1, Calculation.minAreaBasedOnCategory(1));
		assertEquals(MINAREACAT2, Calculation.minAreaBasedOnCategory(2));
		assertEquals(MINAREACAT3, Calculation.minAreaBasedOnCategory(3));
		assertEquals(MINAREACAT4, Calculation.minAreaBasedOnCategory(4));
		assertEquals(MINAREACAT5, Calculation.minAreaBasedOnCategory(5));
	}

	public void testMaxWingLoadBasedOnCagetory() {
		final double DELTA = 0.001;
		assertEquals(MAXWINGLOADCAT1,
				Calculation.maxWingLoadBasedOnCategory(1), DELTA);
		assertEquals(MAXWINGLOADCAT2,
				Calculation.maxWingLoadBasedOnCategory(2), DELTA);
		assertEquals(MAXWINGLOADCAT3,
				Calculation.maxWingLoadBasedOnCategory(3), DELTA);
		assertEquals(MAXWINGLOADCAT4,
				Calculation.maxWingLoadBasedOnCategory(4), DELTA);
		assertEquals(MAXWINGLOADCAT5,
				Calculation.maxWingLoadBasedOnCategory(5), DELTA);
	}

	public void testMinArea() {
		final int LIGHT = 60;
		int minArea = 0;
		// for large exit weight, the min area is given by the category
		assertEquals(MINAREACAT1, Calculation.minArea(1, LIGHT));
		assertEquals(MINAREACAT2, Calculation.minArea(2, LIGHT));
		assertEquals(MINAREACAT3, Calculation.minArea(3, LIGHT));
		assertEquals(MINAREACAT4, Calculation.minArea(4, LIGHT));
		assertEquals(MINAREACAT5, Calculation.minArea(5, LIGHT));

		// for small exit weight , the min area is given by the max wing load

		assertMinAreaForWingload(MAXWINGLOADCAT1);
		assertMinAreaForWingload(MAXWINGLOADCAT2);
		assertMinAreaForWingload(MAXWINGLOADCAT3);
		assertMinAreaForWingload(MAXWINGLOADCAT4);
		assertMinAreaForWingload(MAXWINGLOADCAT5);
	}

	/***
	 * Helper method to check the min area based on the wingload
	 * 
	 * @param maxWingLoad
	 * @param weightInKg
	 */
	private void assertMinAreaForWingload(double maxWingLoad) {
		// high exit weight, so minimal area will be based on wing load limit,
		// not the minimal area for the group
		final int HEAVY = 60;
		int minArea = (int) Math.round((double) Calculation.kgToLbs(HEAVY)
				/ maxWingLoad);

		assertEquals(minArea, Calculation.minArea(1, HEAVY));
	}

}
