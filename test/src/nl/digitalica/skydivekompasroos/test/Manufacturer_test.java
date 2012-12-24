package nl.digitalica.skydivekompasroos.test;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.net.Uri;
import android.test.AndroidTestCase;

import nl.digitalica.skydivekompasroos.Manufacturer;

import junit.framework.TestCase;

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
		Manufacturer m = new Manufacturer("testManufacturer", code, null, null,
				null);
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
				null, null, null, null);
		assertNull(testManufacturer.countryFullName());
	}

}
