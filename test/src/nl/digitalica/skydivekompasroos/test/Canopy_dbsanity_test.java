package nl.digitalica.skydivekompasroos.test;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import nl.digitalica.skydivekompasroos.CanopyType;
import nl.digitalica.skydivekompasroos.Manufacturer;
import android.test.AndroidTestCase;

public class Canopy_dbsanity_test extends AndroidTestCase {

	public void testCanopiesDb() {
		HashMap<UUID, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(getContext());
		HashMap<UUID, UUID> manufacturesNotSeen = new HashMap<UUID, UUID>();
		for (UUID manufacturerId : manufacturers.keySet()) {
			manufacturesNotSeen.put(manufacturerId, manufacturerId);
		}
		// assert we actually do have manufacturers in this list
		assertTrue(manufacturesNotSeen.size() > 0);
		List<CanopyType> canopies = CanopyType.getAllCanopyTypesInList(getContext());
		HashMap<String, String> canopyUniqueNames = new HashMap<String, String>();
		HashMap<UUID, String> canopyIds = new HashMap<UUID, String>();
		for (CanopyType c : canopies) {
			// check Id is unique
			assertFalse("Id is alreay known: " + c.id.toString(),
					canopyIds.containsKey(c.id));
			canopyIds.put(c.id, "seen id");
			// check (calculation) category is within range (1-6)
			assertTrue("category should be 1-6 for " + c.name, c.calculationCategory() >= 1
					&& c.calculationCategory() <= 6);
			// check maxSize is larger than minSize
			if (c.minSize != null && !c.minSize.equals(""))
				if (c.maxSize != null && !c.maxSize.equals("")) {
					int minSize = Integer.parseInt(c.minSize);
					int maxSize = Integer.parseInt(c.maxSize);
					assertTrue("min>max for " + c.uniqueName(),
							minSize <= maxSize);
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
									+ c.uniqueName(),
							firstYearOfProduction <= lastYearOfProduction);
				}
			// check manufacturerAndName is not a duplicate
			String uniqueName = c.uniqueName();
			assertFalse("duplicate manufacturerAndName: " + uniqueName,
					canopyUniqueNames.containsKey(uniqueName));
			canopyUniqueNames.put(uniqueName, uniqueName);
			// check manufacturer has entry in manufactures table
			assertTrue("manufacturer not in manufactures table: ["
					+ c.manufacturerId.toString() + "]",
					manufacturers.containsKey(c.manufacturerId));
			// check every manufacturer in manufacturers list is used
			manufacturesNotSeen.remove(c.manufacturerId);

		}
		// check every manufacturer in manufacturers list is used
		assertEquals(
				"Unused manufacturer(s) " + manufacturesNotSeen.toString(), 0,
				manufacturesNotSeen.size());
	}

	public void testManufacturerDb() {
		HashMap<UUID, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(getContext());
		HashMap<String, String> countryCodes = new HashMap<String, String>();
		UUID everyOtherManufacturerId = UUID
				.fromString(Manufacturer.EVERYOTHERMANUFACTURERIDSTRING);
		for (Manufacturer m : manufacturers.values()) {
			if (!m.id.equals(everyOtherManufacturerId)) {
				// make sure country (if set) can be translated to full name
				String[] countryCodeList = m.countryCode.split(",");
				for (String countryCode : countryCodeList) {
					String trimmedCountryCode = countryCode.trim();
					if (!countryCodes.containsKey(trimmedCountryCode)) {
						Manufacturer testManufacturer = new Manufacturer(
								"test", trimmedCountryCode);
						assertFalse("countrycode not translated "
								+ testManufacturer.countryCode,
								testManufacturer.countryCode
										.equals(testManufacturer
												.countryFullName()));
					}
				}
			}
		}
	}
}
