package nl.digitalica.skydivekompasroos;

public class Calculation {

	// conversion
	final static double WEIGHT_FACTOR_KG_TO_LBS = 2.20462262185;

	// minimal jumps needed for cat of index
	public static final int[] MINIMUMTOTALJUMPS = new int[] { 0, 0, 25, 100,
			400, 700, 1000 };
	public static final int[] MINIMUMJUMPSLAST12MONTHS = new int[] { 0, 0, 10,
			25, 50, 75, 100 };

	/***
	 * Convert a weight in kg to pounds. The result is rounded.
	 * 
	 * @param kg
	 * @return lbs
	 */
	static public int kgToLbs(int kg) {
		return (int) Math.round((double) kg * WEIGHT_FACTOR_KG_TO_LBS);
	}

	/***
	 * Calculate the jumper category based on the total jumps and jumps in last
	 * month
	 * 
	 * @param totalJumps
	 * @param jumpsLast12Months
	 * @return
	 */
	static public int jumperCategory(int totalJumps, int jumpsLast12Months) {
		// TODO: below can be done in a simple loop
		int categoryBasedOnTotalJumps = 0;
		if (totalJumps < MINIMUMTOTALJUMPS[2])
			categoryBasedOnTotalJumps = 1;
		else if (totalJumps < MINIMUMTOTALJUMPS[3])
			categoryBasedOnTotalJumps = 2;
		else if (totalJumps < MINIMUMTOTALJUMPS[4])
			categoryBasedOnTotalJumps = 3;
		else if (totalJumps < MINIMUMTOTALJUMPS[5])
			categoryBasedOnTotalJumps = 4;
		else if (totalJumps < MINIMUMTOTALJUMPS[6])
			categoryBasedOnTotalJumps = 5;
		else
			categoryBasedOnTotalJumps = 6;

		int categoryBasedOnJumpsLast12Months = 0;
		if (jumpsLast12Months < MINIMUMJUMPSLAST12MONTHS[2])
			categoryBasedOnJumpsLast12Months = 1;
		else if (jumpsLast12Months < MINIMUMJUMPSLAST12MONTHS[3])
			categoryBasedOnJumpsLast12Months = 2;
		else if (jumpsLast12Months < MINIMUMJUMPSLAST12MONTHS[4])
			categoryBasedOnJumpsLast12Months = 3;
		else if (jumpsLast12Months < MINIMUMJUMPSLAST12MONTHS[5])
			categoryBasedOnJumpsLast12Months = 4;
		else if (jumpsLast12Months < MINIMUMJUMPSLAST12MONTHS[6])
			categoryBasedOnJumpsLast12Months = 5;
		else
			categoryBasedOnJumpsLast12Months = 6;

		int jumperCategory = Math.min(categoryBasedOnTotalJumps,
				categoryBasedOnJumpsLast12Months);
		return jumperCategory;
	}

	/***
	 * Get the minimal area based on the category note: the special result 0
	 * means there is NO limit
	 * 
	 * @param jumperCategory
	 * @return
	 */
	static public int minAreaBasedOnCategory(int jumperCategory) {
		int minAreaBasedOnCategory = 999; // SHOULD NEVER BE RETURNED

		switch (jumperCategory) {
		case 1:
			minAreaBasedOnCategory = 170;
			break;
		case 2:
			minAreaBasedOnCategory = 170;
			break;
		case 3:
			minAreaBasedOnCategory = 150;
			break;
		case 4:
			minAreaBasedOnCategory = 135;
			break;
		case 5:
			minAreaBasedOnCategory = 120;
			break;
		case 6:
			minAreaBasedOnCategory = 0; // NO LIMIT
			break;
		}
		return minAreaBasedOnCategory;
	}

	/***
	 * Get the max wing load based on the category
	 * 
	 * @param jumperCategory
	 * @return
	 */
	static public double maxWingLoadBasedOnCategory(int jumperCategory) {
		double maxWingload = 9999;

		switch (jumperCategory) {
		case 1:
			maxWingload = 1.1;
			break;
		case 2:
			maxWingload = 1.1;
			break;
		case 3:
			maxWingload = 1.3;
			break;
		case 4:
			maxWingload = 1.5;
			break;
		case 5:
			maxWingload = 1.7;
			break;
		case 6:
			// no limits
			break;
		}
		return maxWingload;
	}

	/***
	 * Get the minimal area of a canopy, from the category of the jumper and his
	 * weight
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 */
	static public int minArea(int jumperCategory, int exitWeightInKg) {
		double maxWingload = maxWingLoadBasedOnCategory(jumperCategory);
		int minAreaBasedOnCategory = minAreaBasedOnCategory(jumperCategory);
		if (minAreaBasedOnCategory == 0) // means there is NO LIMIT
			return minAreaBasedOnCategory;
		int minAreaBasedOnExitWeight = (int) Math.round(kgToLbs(exitWeightInKg)
				/ maxWingload);
		int minArea = Math
				.max(minAreaBasedOnCategory, minAreaBasedOnExitWeight);
		return minArea;
	}

}
