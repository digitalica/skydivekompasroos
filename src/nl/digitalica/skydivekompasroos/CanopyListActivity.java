package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.R.string;
import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CanopyListActivity extends Activity {

	// static, so it can be statically referenced from onClick...
	static StringBuilder skydiveKompasroosResult = new StringBuilder();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canopylist);

		TableLayout canopyTable = (TableLayout) findViewById(R.id.tablelayout_canopylist);

		XmlResourceParser canopiesParser = getResources()
				.getXml(R.xml.canopies);
		int eventType = -1;

		List<Canopy> canopyList = new ArrayList<Canopy>();
		while (eventType != XmlResourceParser.END_DOCUMENT) {
			if (eventType == XmlResourceParser.START_TAG) {
				String strName = canopiesParser.getName();
				if (strName.equals("canopy")) {
					String canopyCategoryString = canopiesParser
							.getAttributeValue(null, "category");
					int canopyCategory;
					canopyCategory = Integer.parseInt(canopyCategoryString);
					String canopyMaker = canopiesParser.getAttributeValue(null,
							"maker");
					String canopyName = canopiesParser.getAttributeValue(null,
							"name");

					canopyList.add(new Canopy(canopyCategory, canopyMaker,
							canopyName));
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

		// sort the canopyList on type, name, maker
		Collections.sort(canopyList, new CanopyComparator());

		Bundle extras = getIntent().getExtras();
		int maxCategory = extras.getInt("CATEGORY");
		int totalJumps = extras.getInt("TOTALJUMPS");
		int jumpsLast12Months = extras.getInt("JUMPSLAST12MONTHS");
		int weight = extras.getInt("WEIGHT");
		int minArea = extras.getInt("MINAREA");

		// now fill table with the list
		String resultHeaderFormat = getString(R.string.shareresultheaderformat);
		String resultheader = String.format(resultHeaderFormat, totalJumps,
				jumpsLast12Months, weight, maxCategory, minArea);
		skydiveKompasroosResult.append(resultheader);

		int lastCat = 999;
		for (Canopy theCanopy : canopyList) {
			if (theCanopy.category != lastCat) {
				// TODO get numbers below from activity params...
				insertCanopyHeaderRow(canopyTable, theCanopy.category,
						maxCategory, minArea);
				lastCat = theCanopy.category;
			}
			insertCanopyRow(canopyTable, theCanopy);
		}
		skydiveKompasroosResult.append(getString(R.string.shareresultfooter));

		// add onclick handler to button
		Button shareResultButton = (Button) findViewById(R.id.buttonShareResult);
		shareResultButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent sendIntent = new Intent();
				sendIntent.setAction(Intent.ACTION_SEND);
				sendIntent.putExtra(Intent.EXTRA_TEXT,
						CanopyListActivity.skydiveKompasroosResult());
				sendIntent.putExtra(Intent.EXTRA_SUBJECT,
						getString(R.string.shareresultsubject));
				sendIntent.putExtra(Intent.EXTRA_TITLE,
						getString(R.string.shareresultsubject));
				sendIntent.setType("text/plain");
				startActivity(sendIntent);

			}
		});

	}

	static String skydiveKompasroosResult() {
		return skydiveKompasroosResult.toString();
	}

	/***
	 * Add a header row (actually multiple rows) to both the table on screen and
	 * the stringbuilder text result.
	 * 
	 * @param canopyTable
	 * @param category
	 * @param maxCategory
	 * @param minArea
	 */
	private void insertCanopyHeaderRow(TableLayout canopyTable, int category,
			int maxCategory, int minArea) {

		// create text view and row
		TextView textView;
		TableRow.LayoutParams params = new TableRow.LayoutParams();
		params.span = 2;
		TableRow row = new TableRow(this);
		skydiveKompasroosResult.append(System.getProperty("line.separator"));

		// add first row
		textView = new TextView(this);
		String headerFormat = getString(R.string.canopyListHeader);
		String headerText = String.format(headerFormat, category);
		textView.setText(headerText);
		textView.setTypeface(null, Typeface.BOLD);
		textView.setTextSize(getResources().getDimension(R.dimen.header1));
		textView.setLayoutParams(params);
		row.addView(textView);
		// add row to table
		canopyTable.addView(row);
		skydiveKompasroosResult.append(headerText.toUpperCase());

		// add second row
		row = new TableRow(this);
		textView = new TextView(this);
		String subHeader;
		if (category <= maxCategory) {
			String subHeaderFormat = getString(R.string.canopyListHeaderOk);
			subHeader = String.format(subHeaderFormat, minArea);
			textView.setText(subHeader);
		} else {
			subHeader = getString(R.string.canopyListHeaderNotOk);
			textView.setText(subHeader);
			textView.setTextColor(getResources().getColor(R.color.Cat1Red));
		}
		textView.setTypeface(null, Typeface.BOLD);
		textView.setLayoutParams(params);
		row.addView(textView);
		skydiveKompasroosResult.append(": " + subHeader
				+ System.getProperty("line.separator"));

		// add row to table
		canopyTable.addView(row);

		row = new TableRow(this);
		textView = new TextView(this);
		textView.setText(getString(R.string.canopyType));
		textView.setTypeface(null, Typeface.BOLD);
		row.addView(textView);
		textView = new TextView(this);
		textView.setText(getString(R.string.canopyMaker));
		textView.setTypeface(null, Typeface.BOLD);
		row.addView(textView);
		canopyTable.addView(row);

	}

	private void insertCanopyRow(TableLayout canopyTable, Canopy theCanopy) {
		// create text view and row
		TableRow row = new TableRow(this);
		TextView textView;

		// TODO: change the layout of these rows
		textView = new TextView(this);
		textView.setText(theCanopy.name);
		row.addView(textView);
		textView = new TextView(this);
		textView.setText(theCanopy.maker);
		row.addView(textView);

		// add row to table
		canopyTable.addView(row);
		skydiveKompasroosResult.append(theCanopy.name + " - " + theCanopy.maker
				+ System.getProperty("line.separator"));

	}

	private class Canopy {
		public int category;
		public String maker;
		public String name;

		public Canopy(int canopyCategory, String canopyMaker, String canopyName) {
			this.category = canopyCategory;
			this.maker = canopyMaker;
			this.name = canopyName;
		}

	}

	public class CanopyComparator implements Comparator {

		public int compare(Object o1, Object o2) {
			Canopy c1 = (Canopy) o1;
			Canopy c2 = (Canopy) o2;
			if (c1.category != c2.category)
				return c1.category < c2.category ? -1 : 1;
			int result = c1.name.compareTo(c2.name);
			if (result != 0)
				return result;
			return c1.maker.compareTo(c2.maker);
		}

	}

}
