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

	final public static String DEFAULTMANUFACTURER = "Some manufacturer";
	final public static String DEFAULTSIZE = "170";

	// results
	public final static int ACCEPTABLE = 0;
	public final static int NEEDEDSIZENOTAVAILABLE = 1;
	public final static int CATEGORYTOOHIGH = 2;

	// properties
	public int category;
	public String manufacturer;
	public String name;
	public String url;
	public String cells;
	public boolean commontype;
	public String dropzoneId;
	public String minSize;
	public String maxSize;
	public String firstYearOfProduction;
	public String lastYearOfProduction;
	private String remarks;
	private String remarks_nl;
	// TODO: the below should be a boolean probably...
	public int isSpecialCatchAllCanopy = 0;

	public Canopy(int canopyCategory, String canopyManufacturer,
			String canopyName, String canopyUrl, String canopyCells,
			boolean canopyCommonType, String canopyDropzoneId,
			String canopyMinSize, String canopyMaxSize,
			String canopyFirstYearOfProduction,
			String canopyLastYearOfProduction, String canopyRemarks,
			String canopyRemarks_nl, int isSpecialCatchAllCanopy) {
		this.category = canopyCategory;
		this.manufacturer = canopyManufacturer;
		this.name = canopyName;
		this.url = canopyUrl;
		this.cells = canopyCells;
		this.commontype = canopyCommonType;
		this.dropzoneId = canopyDropzoneId;
		this.minSize = canopyMinSize;
		this.maxSize = canopyMaxSize;
		this.firstYearOfProduction = canopyFirstYearOfProduction;
		this.lastYearOfProduction = canopyLastYearOfProduction;
		this.remarks = canopyRemarks;
		this.remarks_nl = canopyRemarks_nl;
		this.isSpecialCatchAllCanopy = isSpecialCatchAllCanopy;
	}

	/***
	 * Constructor to create a specific canopy (mostly convenient for testing)
	 * 
	 * @param canopyCategory
	 * @param canopyName
	 * @param size
	 */
	public Canopy(int canopyCategory, String canopyName, String size) {
		this(canopyCategory, DEFAULTMANUFACTURER, canopyName, null, null, true,
				null, size, size, null, null, null, null, 0);
	}

	/***
	 * Constructor to create a specific canopy (mostly convenient for testing)
	 * 
	 * @param canopyCategory
	 * @param canopyName
	 * @param size
	 */
	public Canopy(int canopyCategory, String canopyName) {
		this(canopyCategory, DEFAULTMANUFACTURER, canopyName, null, null, true,
				null, DEFAULTSIZE, DEFAULTSIZE, null, null, null, null, 0);
	}

	/***
	 * Determine if we would like to know more details about this canopy used to
	 * decide if a text should be shown in Canopy Details screen
	 * 
	 * @return
	 */
	public boolean addtionalInformationNeeded() {
		if (this.firstYearOfProduction == null
				|| this.firstYearOfProduction.equals(""))
			return true;

		if (this.cells == null || this.cells.equals(""))
			return true;

		if (this.minSize == null || this.minSize.equals(""))
			return true;

		if (this.maxSize == null || this.maxSize.equals(""))
			return true;

		// seems we know all we want to...
		return false;
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
					String canopyCommonTypeString = canopiesParser
							.getAttributeValue(null, "commontype");
					String canopyDropzoneId = canopiesParser.getAttributeValue(
							null, "dropzoneid");
					boolean canopyCommonType = true;
					if (Integer.parseInt(canopyCommonTypeString) == 0)
						canopyCommonType = false;
					String canopyMinSize = canopiesParser.getAttributeValue(
							null, "minsize");
					String canopyMaxSize = canopiesParser.getAttributeValue(
							null, "maxsize");
					String canopyFirstyearOfProduction = canopiesParser
							.getAttributeValue(null, "firstyearofproduction");
					String canopyLastyearOfProduction = canopiesParser
							.getAttributeValue(null, "lastyearofproduction");
					String canopyRemarks = canopiesParser.getAttributeValue(
							null, "remarks");
					String canopyRemarks_nl = canopiesParser.getAttributeValue(
							null, "remarks_nl");
					String isSpecialCatchAllCanopyString = canopiesParser
							.getAttributeValue(null, "isspecialcatchallcanopy");
					int isSpecialCatchAllCanopy = 0;
					if (isSpecialCatchAllCanopyString != ""
							&& isSpecialCatchAllCanopyString != null)
						isSpecialCatchAllCanopy = Integer
								.parseInt(isSpecialCatchAllCanopyString);
					Canopy canopy = new Canopy(canopyCategory,
							canopyManufacturer, canopyName, canopyUrl,
							canopyCells, canopyCommonType, canopyDropzoneId,
							canopyMinSize, canopyMaxSize,
							canopyFirstyearOfProduction,
							canopyLastyearOfProduction, canopyRemarks,
							canopyRemarks_nl, isSpecialCatchAllCanopy);
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
	 * Comparator, to be used for sorting For each manufacturer we sort on cat
	 * first, so the colored bars will show up nicely in list and help
	 * separating suppliers
	 * 
	 * @author robbert
	 */
	public static class ComparatorByManufacturerCategoryName implements
			Comparator {

		public int compare(Object o1, Object o2) {
			Canopy c1 = (Canopy) o1;
			Canopy c2 = (Canopy) o2;
			if (c1.isSpecialCatchAllCanopy == 1)
				return 1;
			if (c2.isSpecialCatchAllCanopy == 1)
				return -1;
			if (c1.manufacturer != c2.manufacturer)
				return c1.manufacturer.compareTo(c2.manufacturer);
			if (c1.category != c2.category)
				return c1.category < c2.category ? -1 : 1;
			return c1.name.compareTo(c2.name);
		}
	}

	@Override
	public String toString() {
		return Integer.toString(this.category) + " " + this.name + " ("
				+ this.manufacturer + ")";
	}

	/***
	 * Determines if a canopy is acceptable for a given jumper
	 * 
	 * @param jumperCategory
	 * @param exitWeightInKg
	 * @return
	 * 
	 *         TODO: should return an enum, not an int!!!!!
	 */
	public int acceptablility(int jumperCategory, int exitWeightInKg) {
		if (jumperCategory < this.category)
			return CATEGORYTOOHIGH; // not acceptable
		if (this.maxSize != "" && this.maxSize != null)
			if (Integer.parseInt(this.maxSize) < Calculation.minArea(
					jumperCategory, exitWeightInKg))
				return NEEDEDSIZENOTAVAILABLE;
		return ACCEPTABLE;
	}

	/**
	 * returns the url in dropzone.com for this canopy
	 * 
	 * @return
	 */
	public String dropZoneUrl() {
		String url = "";
		if (dropzoneId != null && !dropzoneId.equals(""))
			url = "http://www.dropzone.com/gear/Detailed/" + dropzoneId
					+ ".html";
		return url;
	}

	/**
	 * returns a text showing the number of cells and min/max size, to use in
	 * the canopy list, as an alternative to the manufacturer when the sorting
	 * is by manufacturer
	 * 
	 * @return
	 */
	public String alternativeDetailsText(Context c) {
		String detailsText = "";
		if (cells != null)
			detailsText = String.format(c.getString(R.string.alternativeCells),
					cells);

		if (minSize != null && maxSize != null && minSize != ""
				&& maxSize != "") {
			if (!detailsText.equals(""))
				detailsText += ", ";
			detailsText += String.format(
					c.getString(R.string.alternativeSizes), minSize, maxSize);
		}
		return detailsText;
	}

	/***
	 * Return remarks in current locale
	 * 
	 * @return
	 */
	public String remarks() {
		boolean dutch = Calculation.isLanguageDutch();
		return remarks(dutch);
	}

	/***
	 * Return remarks in Dutch or English
	 * 
	 * @param inDutch
	 * @return
	 */
	public String remarks(boolean inDutch) {
		return inDutch ? this.remarks_nl : this.remarks;
	}

}
