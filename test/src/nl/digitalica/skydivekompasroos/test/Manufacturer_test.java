package nl.digitalica.skydivekompasroos.test;

import java.util.Locale;

import nl.digitalica.skydivekompasroos.Manufacturer;
import android.test.AndroidTestCase;

public class Manufacturer_test extends AndroidTestCase {

	public void testCountry() {
		// sanity check and get full names
		String frFullName = assertFullNameForCountryCode("fr");
		String usFullName = assertFullNameForCountryCode("us");

		// check combined name
		String usfrFullName = assertFullNameForCountryCode("us, fr");
		assertEquals(usFullName + ", " + frFullName, usfrFullName);

		// simple sanity check on rest
		assertFullNameForCountryCode("de");
		assertFullNameForCountryCode("sa");
	}

	private String assertFullNameForCountryCode(String code) {
		Locale locale;
		Manufacturer m = new Manufacturer("testManufacturer", code);
		String countryFullName = m.countryFullName();
		assertNotNull(countryFullName);
		assertFalse("Full name equals code for " + code,
				countryFullName.equals(code));
		assertFalse("Full name empty for " + code, countryFullName.equals(""));

		Locale orgLocale = Locale.getDefault();
		locale = new Locale("us");
		Locale.setDefault(locale);
		String english = m.countryFullName();
		locale = new Locale("nl");
		Locale.setDefault(locale);
		String dutch = m.countryFullName();
		assertFalse("Dutch & English for " + code + " are equal",
				dutch.equals(english));
		Locale.setDefault(orgLocale);

		return countryFullName;
	}

	public void testCountryFullNameForNull() {
		Manufacturer testManufacturer = new Manufacturer("testManufacturer",
				null);
		assertNull(testManufacturer.countryFullName());
	}

}
