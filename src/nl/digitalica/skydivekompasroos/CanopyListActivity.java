package nl.digitalica.skydivekompasroos;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CanopyListActivity extends KompasroosBaseActivity {

	static final int SORTBYNAME = 1;
	static final int SORTBYMANUFACTURER = 2;
	static final int SORTBYCATEGORY = 3;

	List<Canopy> canopyList;
	TableLayout canopyTable;

	// static, so it can be statically referenced from onClick...
	static StringBuilder skydiveKompasroosResult = new StringBuilder();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canopylist);

		canopyTable = (TableLayout) findViewById(R.id.tablelayout_canopylist);

		canopyList = Canopy.getAllCanopiesInList(CanopyListActivity.this);

		Bundle extras = getIntent().getExtras();

		// now fill table with the list
		String resultHeaderFormat = getString(R.string.shareresultheaderformat);
		String resultheader = String.format(resultHeaderFormat,
				currentTotalJumps, currentJumpsLast12Months, currentWeight,
				currentMaxCategory, currentMinArea);
		skydiveKompasroosResult.append(resultheader);

		// TODO: store sorting so it is persistent (?)
		fillCanopyTable(canopyTable, SORTBYNAME);

		// add onclick handler to button
		Button shareResultButton = (Button) findViewById(R.id.buttonShareResult);
		shareResultButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareResult();
			}

		});

	}

	/***
	 * Fills the canopy table on screen again based on the given sorting method
	 * 
	 * @param canopyTable
	 * @param sortingMethod
	 */
	private void fillCanopyTable(TableLayout canopyTable, int sortingMethod) {
		// sort the canopyList on type, name, manufacturer
		switch (sortingMethod) {
		case SORTBYNAME:
			Collections.sort(canopyList,
					new Canopy.ComparatorByNameManufacturer());
			break;
		case SORTBYCATEGORY:
			Collections.sort(canopyList, new Canopy.ComparatorByCategoryName());
			break;
		case SORTBYMANUFACTURER:
			Collections.sort(canopyList,
					new Canopy.ComparatorByManufacturerName());
			break;
		}
		canopyTable.removeAllViewsInLayout();
		for (Canopy theCanopy : canopyList)
			insertCanopyRow(canopyTable, theCanopy, currentMaxCategory);
		skydiveKompasroosResult.append(getString(R.string.shareresultfooter));
	}

	static String skydiveKompasroosResult() {
		return skydiveKompasroosResult.toString();
	}

	private void shareResult() {
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_canopylist, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_shareResult:
			shareResult();
			return true;
		case R.id.menu_sortByName:
			fillCanopyTable(canopyTable, SORTBYNAME);
			return true;
		case R.id.menu_sortByManufacturer:
			fillCanopyTable(canopyTable, SORTBYMANUFACTURER);
			return true;
		case R.id.menu_sortByCategory:
			fillCanopyTable(canopyTable, SORTBYCATEGORY);
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Generic click handle to for clicks on a canopy row the tag is used to
	 * decide what canopy details to show.
	 * 
	 * @param v
	 */
	private void onCanopyRowClick(View v) {
		String canopyKey = v.getTag().toString();
		Intent i = new Intent(getBaseContext(), CanopyDetailsActivity.class);
		// TODO: remove extras as they will be in global vars...
		i.putExtra(CANOPYKEYEXTRA, canopyKey);
		startActivity(i);
	}

	/***
	 * Add a canopy row to the canopy table
	 * 
	 * @param canopyTable
	 * @param theCanopy
	 * @param maxCategory
	 */
	private void insertCanopyRow(TableLayout canopyTable, Canopy theCanopy,
			int maxCategory) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View canopyListRow = inflater.inflate(R.layout.canopy_row_layout, null);

		Drawable box = getResources().getDrawable(R.drawable.box);
		LinearLayout hLayout = (LinearLayout) canopyListRow
				.findViewById(R.id.linearLayoutCanopyListRow);
		hLayout.setTag(theCanopy.key());
		hLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCanopyRowClick(v);
			}
		});

		hLayout.setBackgroundColor(backgroundColorForAcceptance(maxCategory >= theCanopy.category));

		// hLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));

		TextView tvCategory = (TextView) canopyListRow
				.findViewById(R.id.textViewCanopyListRowCategory);
		// tvCategory.setBackgroundDrawable(box);
		tvCategory.setText(Integer.toString(theCanopy.category));

		TextView tvCanopyName = (TextView) canopyListRow
				.findViewById(R.id.textViewCanopyListRowName);
		// tvCanopyName.setBackgroundDrawable(box);
		tvCanopyName.setText(theCanopy.name);

		// TODO: add more details like cells and remarks here
		TextView tvCanopyDetails = (TextView) canopyListRow
				.findViewById(R.id.textViewCanopyListRowDetails);
		tvCanopyDetails.setText(theCanopy.manufacturer);
		// tvCanopyDetails.setBackgroundDrawable(box);

		// create text view and row
		TableRow row = new TableRow(this);
		row.addView(canopyListRow);

		// add row to table
		canopyTable.addView(row);

		// add row to text.
		skydiveKompasroosResult
				.append(theCanopy.name + " - " + theCanopy.manufacturer
						+ System.getProperty("line.separator"));

	}
}
