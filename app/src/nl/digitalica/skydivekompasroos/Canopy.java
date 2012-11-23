package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;

public class Canopy {
	public int category;
	public String manufacturer;
	public String name;
	public String url;
	public String cells;
	public String minSize;
	public String maxSize;
	public String remarks;
	// TODO: the below should be a boolean probably...
	public int isSpecialCatchAllCanopy = 0;

	public Canopy(int canopyCategory, String canopyManufacturer,
			String canopyName, String canopyUrl, String canopyCells,
			String canopyMinSize, String canopyMaxSize, String canopyRemarks,
			int isSpecialCatchAllCanopy) {
		this.category = canopyCategory;
		this.manufacturer = canopyManufacturer;
		this.name = canopyName;
		this.url = canopyUrl;
		this.cells = canopyCells;
		this.minSize = canopyMinSize;
		this.maxSize = canopyMaxSize;
		this.remarks = canopyRemarks;
		this.isSpecialCatchAllCanopy = isSpecialCatchAllCanopy;
	}

	/***
	 * Reads all canopies from the XML in a list. This is ok as the number will
	 * always be limited anyway
	 * 
	 * @return
	 */
	static public List<Canopy> getAllCanopiesInList(Context context) {
		return getCanopiesInList(null, context);
	}

	static public List<Canopy> getCanopiesInList(String key, Context context) {
		XmlResourceParser canopiesParser = context.getResources().getXml(
				R.xml.canopies);
		int eventType = -1;

		List<Canopy> canopyList = new ArrayList<Canopy>();
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				String strName = canopiesParser.getName();
				if (strName.equals("canopy")) {
					String canopyCategoryString = canopiesParser
							.getAttributeValue(null, "category");
					int canopyCategory;
					try {
						canopyCategory = Integer.parseInt(canopyCategoryString);
					} catch (NumberFormatException e) {
						throw new RuntimeException("Canopy category no Int", e);
					}
					String canopyManufacturer = canopiesParser
							.getAttributeValue(null, "manufacturer");
					String canopyName = canopiesParser.getAttributeValue(null,
							"name");
					String canopyUrl = canopiesParser.getAttributeValue(null,
							"url");
					String canopyCells = canopiesParser.getAttributeValue(null,
							"cells");
					String canopyMinSize = canopiesParser.getAttributeValue(
							null, "minsize");
					String canopyMaxSize = canopiesParser.getAttributeValue(
							null, "maxsize");
					String canopyRemarks = canopiesParser.getAttributeValue(
							null, "remarks");
					String isSpecialCatchAllCanopyString = canopiesParser
							.getAttributeValue(null, "isspecialcatchallcanopy");
					int isSpecialCatchAllCanopy = 0;
					if (isSpecialCatchAllCanopyString != ""
							&& isSpecialCatchAllCanopyString != null)
						isSpecialCatchAllCanopy = Integer
								.parseInt(isSpecialCatchAllCanopyString);
					Canopy canopy = new Canopy(canopyCategory,
							canopyManufacturer, canopyName, canopyUrl,
							canopyCells, canopyMinSize, canopyMaxSize,
							canopyRemarks, isSpecialCatchAllCanopy);
					if (key == null)
						canopyList.add(canopy);
					else if (canopy.key().equals(key)) {
						canopyList.add(canopy);
						return canopyList;
					}
				}

			}
			try {
				eventType = canopiesParser.next();
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return canopyList;
	}

	/***
	 * Returns a unique key for this canopy
	 * 
	 * @return
	 */
	public String key() {
		return manufacturer + '|' + name;
	}

	/***
	 * Comparator, to be used for sorting
	 * 
	 * @author robbert
	 */
	public static class ComparatorByCategoryName implements Comparator {

		public int compare(Object o1, Object o2) {
			Canopy c1 = (Canopy) o1;
			Canopy c2 = (Canopy) o2;
			if (c1.isSpecialCatchAllCanopy == 1)
				return 1;
			if (c2.isSpecialCatchAllCanopy == 1)
				return -1;
			if (c1.category != c2.category)
				return c1.category < c2.category ? -1 : 1;
			int result = c1.name.compareTo(c2.name);
			if (result != 0)
				return result;
			return c1.manufacturer.compareTo(c2.manufacturer);
		}

	}

	/***
	 * Comparator, to be used for sorting
	 * 
	 * @author robbert
	 */
	public static class ComparatorByNameManufacturer implements Comparator {

		public int compare(Object o1, Object o2) {
			Canopy c1 = (Canopy) o1;
			Canopy c2 = (Canopy) o2;
			if (c1.isSpecialCatchAllCanopy == 1)
				return 1;
			if (c2.isSpecialCatchAllCanopy == 1)
				return -1;
			if (c1.name != c2.name)
				return c1.name.compareTo(c2.name);
			return c1.manufacturer.compareTo(c2.manufacturer);
		}

	}

	/***
	 * Comparator, to be used for sorting
	 * 
	 * @author robbert
	 */
	public static class ComparatorByManufacturerName implements Comparator {

		public int compare(Object o1, Object o2) {
			Canopy c1 = (Canopy) o1;
			Canopy c2 = (Canopy) o2;
			if (c1.isSpecialCatchAllCanopy == 1)
				return 1;
			if (c2.isSpecialCatchAllCanopy == 1)
				return -1;
			if (c1.manufacturer != c2.manufacturer)
				return c1.manufacturer.compareTo(c2.manufacturer);
			return c1.name.compareTo(c2.name);
		}

	}

}
