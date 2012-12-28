package nl.digitalica.skydivekompasroos.test;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import nl.digitalica.skydivekompasroos.Canopy;
import nl.digitalica.skydivekompasroos.Manufacturer;
import android.test.AndroidTestCase;
import android.util.Log;

public class CanopyDbExportToCsv extends AndroidTestCase {

	private static final String SEPARATOR = ",";
	private static final String CSVFILENAME = "KompasroosAppParachutes.csv";
	private static final String QUOTE = "\"";
	private static final String TAG = "CSV";

	/**
	 * We manually mark this as a test so it can be executed
	 * 
	 * @throws IOException
	 */
	public void testExportCanopies() throws IOException {
		List<Canopy> canopies = Canopy.getAllCanopiesInList(getContext());
		HashMap<UUID, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(getContext());

		Log.v(TAG,
				",cat,name,manufacturer,country,cells,minsize,maxsize,firstYear,lastYear,isCommon,remarks manufacturer,remarks canopy,url manufacturer, url canopy,remarks manufacturer nl, remarks canopy nl");

		for (Canopy c : canopies) {
			Manufacturer m = manufacturers.get(c.manufacturerId);
			StringBuilder line = new StringBuilder();
			line.append(SEPARATOR); // convenient, to remove other logcat cols.
			line.append(Integer.toString(c.category) + SEPARATOR);
			line.append(c.name + SEPARATOR);
			line.append(c.manufacturerName + SEPARATOR);
			if (m != null) // add with quotes for multiple countries
				line.append(QUOTE + m.countryFullName() + QUOTE + SEPARATOR);
			else
				line.append(SEPARATOR);
			line.append((c.cells == null ? "" : c.cells) + SEPARATOR);
			line.append((c.minSize == null ? "" : c.minSize) + SEPARATOR);
			line.append((c.maxSize == null ? "" : c.maxSize) + SEPARATOR);
			line.append((c.firstYearOfProduction == null ? ""
					: c.firstYearOfProduction) + SEPARATOR);
			line.append((c.lastYearOfProduction == null ? ""
					: c.lastYearOfProduction) + SEPARATOR);
			line.append((c.commontype ? "yes" : "no") + SEPARATOR);
			if (m != null) // add with quotes for complicated remarks
				line.append((m.remarks(false) == null ? "" : QUOTE
						+ m.remarks(false) + QUOTE)
						+ SEPARATOR);
			else
				line.append(SEPARATOR);
			line.append((c.remarks(false) == null ? "" : QUOTE
					+ c.remarks(false) + QUOTE)
					+ SEPARATOR);
			if (m != null) // add with quotes
				line.append((m.url == null ? "" : m.url) + SEPARATOR);
			else
				line.append(SEPARATOR);
			line.append((c.url == null ? "" : c.url) + SEPARATOR);
			if (m != null) // add with quotes for complicated remarks
				line.append((m.remarks(true) == null ? "" : QUOTE
						+ m.remarks(true) + QUOTE)
						+ SEPARATOR);
			else
				line.append(SEPARATOR);
			line.append((c.remarks(true) == null ? "" : QUOTE + c.remarks(true)
					+ QUOTE)
					+ SEPARATOR);
			Log.v(TAG, line.toString());
		}

	}
}