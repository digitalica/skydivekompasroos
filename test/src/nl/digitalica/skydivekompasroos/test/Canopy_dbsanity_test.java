package nl.digitalica.skydivekompasroos.test;

import java.util.HashMap;
import java.util.List;

import nl.digitalica.skydivekompasroos.Canopy;
import nl.digitalica.skydivekompasroos.Manufacturer;
import android.test.AndroidTestCase;

public class Canopy_dbsanity_test extends AndroidTestCase {

	public void testCanopiesDb() {
		HashMap<String, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(getContext());
		HashMap<String, String> manufacturesNotSeen = new HashMap<String, String>();
		for (String manufacturer : manufacturers.keySet()) {
			manufacturesNotSeen.put(manufacturer, manufacturer);
		}
		// assert we actually do have manufacturers in this list
		assertTrue(manufacturesNotSeen.size() > 0);
		List<Canopy> canopies = Canopy.getAllCanopiesInList(getContext());
		HashMap<String, String> canopyKeys = new HashMap<String, String>();
		for (Canopy c : canopies) {
			// check maxSize is larger than minSize
			if (c.minSize != null && !c.minSize.equals(""))
				if (c.maxSize != null && !c.maxSize.equals("")) {
					int minSize = Integer.parseInt(c.minSize);
					int maxSize = Integer.parseInt(c.maxSize);
					assertTrue("min>max for " + c.key(), minSize <= maxSize);
				}
			// check last year is larger then first year of production
			if (c.firstYearOfProduction != null
					&& !c.firstYearOfProduction.equals(""))
				if (c.lastYearOfProduction != null
						&& !c.lastYearOfProduction.equals("")) {
					int firstYearOfProduction = Integer
							.parseInt(c.firstYearOfProduction);
					int lastYearOfProduction = Integer
							.parseInt(c.lastYearOfProduction);
					assertTrue(
							"firstYearOfProduction>lastYearOfProduction for "
									+ c.key(),
							firstYearOfProduction <= lastYearOfProduction);
				}
			// check key is not a duplicate
			String key = c.key();
			assertFalse("duplicate key: " + key, canopyKeys.containsKey(key));
			canopyKeys.put(key, key);
			// check manufacturer has entry in manufactures table
			if (c.isSpecialCatchAllCanopy != 1)
				assertTrue("manufacturer not in manufactures table: ["
						+ c.manufacturer + "]",
						manufacturers.containsKey(c.manufacturer));
			// check every manufacturer in manufacturers list is used
			manufacturesNotSeen.remove(c.manufacturer);

		}
		// check every manufacturer in manufacturers list is used
		assertEquals(
				"Unused manufacturer(s) " + manufacturesNotSeen.toString(), 0,
				manufacturesNotSeen.size());
	}

	public void testManufacturerDb() {
		HashMap<String, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(getContext());
		HashMap<String, String> countryCodes = new HashMap<String, String>();
		for (Manufacturer m : manufacturers.values())
			// make sure country (if set) can be translated to full name
			if (m.countryCode != null)
				assertFalse("countrycode not translated " + m.countryCode,
						m.countryCode.equals(m.countryFullName()));
	}
}
