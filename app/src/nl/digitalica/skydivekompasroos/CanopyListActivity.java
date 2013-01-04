package nl.digitalica.skydivekompasroos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TableRow;
import android.widget.TextView;

public class CanopyListActivity extends KompasroosBaseActivity {

	public enum SortingEnum {
		SORTBYNAME, SORTBYMANUFACTURER, SORTBYCATEGORY
	}

	public enum FilterEnum {
		COMMONAROUNDMAX, ONLYCOMMON, ALL
	}

	List<Canopy> canopyList;
	LinearLayout canopyTable;

	SortingEnum currentSortingMethod;
	FilterEnum currentFilterType;

	final static int SORT_DIALOG_ID = 1;
	final static int FILTER_DIALOG_ID = 2;

	final static int MILLISINDAY = 1000 * 60 * 60 * 24;

	// static, so it can be statically referenced from onClick...
	static StringBuilder skydiveKompasroosResultAccepted;
	static StringBuilder skydiveKompasroosResultNeededSizeNotAvailable;
	static StringBuilder skydiveKompasroosResultNotAccepted;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canopylist);

		canopyTable = (LinearLayout) findViewById(R.id.tablelayout_canopylist);

		canopyList = Canopy.getAllCanopiesInList(CanopyListActivity.this);

		// if sorting and filter were save over 1 day ago, clear them
		int sortingFilterTime = prefs.getInt(SETTING_SORTING_FILTER_DAYNR, 0);
		int currentTime = (int) (System.currentTimeMillis() / MILLISINDAY);
		if (currentTime - sortingFilterTime > 7) {
			Editor e = prefs.edit();
			e.remove(SETTING_SORTING);
			e.remove(SETTING_FILTER_TYPE);
			e.commit();
		}

		// get the saved sorting Method
		int sortingMethodOrdinal = prefs.getInt(SETTING_SORTING,
				SortingEnum.SORTBYNAME.ordinal());
		this.currentSortingMethod = SortingEnum.values()[sortingMethodOrdinal];

		// get the saved filter cat
		int filterCatdOrdinal = prefs.getInt(SETTING_FILTER_TYPE,
				FilterEnum.COMMONAROUNDMAX.ordinal());
		this.currentFilterType = FilterEnum.values()[filterCatdOrdinal];

		// TODO: store sorting so it is persistent (?)
		fillCanopyTable(canopyTable, currentSortingMethod, currentFilterType);

		// add on click handler to share button
		ImageButton shareResultButton = (ImageButton) findViewById(R.id.buttonShareResult);
		shareResultButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareResult();
			}

		});

		// add on click handler to filter header
		View filterHeader = findViewById(R.id.tablelayout_filterheader);
		filterHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(FILTER_DIALOG_ID);
			}
		});

		// add on click handler to filter button
		ImageButton filterButton = (ImageButton) findViewById(R.id.buttonEditFilter);
		filterButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				showDialog(FILTER_DIALOG_ID);
			}
		});

	}

	/***
	 * Fills the canopy table on screen again based on the given sorting method
	 * 
	 * @param canopyTable
	 * @param sortingMethod
	 */
	private void fillCanopyTable(LinearLayout canopyTable,
			SortingEnum sortingMethod, FilterEnum filterType) {

		// sort the canopyList on type, name, manufacturer
		Comparator<Canopy> canopyComparator = null;
		switch (sortingMethod) {
		case SORTBYNAME:
			canopyComparator = new Canopy.ComparatorByNameManufacturer();
			break;
		case SORTBYCATEGORY:
			canopyComparator = new Canopy.ComparatorByCategoryName();
			break;
		case SORTBYMANUFACTURER:
			canopyComparator = new Canopy.ComparatorByManufacturerCategoryName();
			break;
		}
		Collections.sort(canopyList, canopyComparator);

		this.currentSortingMethod = sortingMethod;
		savePreference(SETTING_SORTING, sortingMethod.ordinal());
		this.currentFilterType = filterType;
		savePreference(SETTING_FILTER_TYPE, filterType.ordinal());
		savePreference(SETTING_SORTING_FILTER_DAYNR,
				(int) (System.currentTimeMillis() / MILLISINDAY));
		canopyTable.removeAllViewsInLayout();
		skydiveKompasroosResultAccepted = new StringBuilder();
		skydiveKompasroosResultNeededSizeNotAvailable = new StringBuilder();
		skydiveKompasroosResultNotAccepted = new StringBuilder();

		UUID previousManufacturerId = UUID.randomUUID();
		int previousCat = 9999;
		int shownCount = 0;
		int allCount = 0;
		for (Canopy theCanopy : canopyList) {
			allCount++;
			boolean showThisCanopy = true;
			// check cat filter
			if (filterType == FilterEnum.ONLYCOMMON)
				if (!theCanopy.commontype)
					showThisCanopy = false;
			if (filterType == FilterEnum.COMMONAROUNDMAX)
				if (!theCanopy.commontype
						|| theCanopy.category < currentMaxCategory - 1
						|| theCanopy.category > currentMaxCategory + 1)
					showThisCanopy = false;
			// show the canopy (and maybe headerline) if needed
			if (showThisCanopy) {
				shownCount++;
				if (sortingMethod == SortingEnum.SORTBYMANUFACTURER
						&& !previousManufacturerId
								.equals(theCanopy.manufacturerId)) {
					insertCanopyHeaderRow(canopyTable,
							theCanopy.manufacturerName);
					previousManufacturerId = theCanopy.manufacturerId;
				}
				if (sortingMethod == SortingEnum.SORTBYCATEGORY
						&& previousCat != theCanopy.category) {
					insertCanopyHeaderRow(canopyTable, String.format(
							getString(R.string.canopyListCategoryHeader),
							theCanopy.category));
					previousCat = theCanopy.category;
				}
				insertCanopyRow(canopyTable, theCanopy, currentMaxCategory,
						currentWeight);
			}
		}
		String nl = System.getProperty("line.separator");

		TextView tvFilterText = (TextView) findViewById(R.id.textview_filtersettings);
		StringBuilder filterText = new StringBuilder();
		if (allCount != shownCount)
			filterText.append(String.format(getString(R.string.filterRange),
					shownCount, allCount) + nl);
		else
			filterText.append(getResources().getString(R.string.filterNone)
					+ nl);
		switch (filterType) {
		case ALL:
			filterText.append(String.format(
					getResources().getString(R.string.filterTypeAll), allCount)
					+ nl);
			break;
		case COMMONAROUNDMAX:
			filterText.append(String.format(
					getResources().getString(R.string.filterTypeAround),
					currentMaxCategory) + nl);
			break;
		case ONLYCOMMON:
			filterText.append(getResources().getString(
					R.string.filterTypeCommon)
					+ nl);
			break;
		}
		tvFilterText.setText(filterText.toString());
	}

	/***
	 * Return the full string for the results to share. It has one block for the
	 * acceptable canopies, and one for the not acceptable, as colored
	 * backgrounds are not an option here...
	 * 
	 * @param c
	 * @return
	 */
	static String skydiveKompasroosResult(Context c) {
		String nl = System.getProperty("line.separator");
		String nlnl = nl + nl;
		StringBuilder skydiveKompasroosResult = new StringBuilder();
		// now fill table with the list
		String resultHeaderFormat = c
				.getString(R.string.shareresultheaderformat);
		String resultheader = String.format(resultHeaderFormat,
				currentTotalJumps, currentJumpsLast12Months, currentWeight,
				currentMaxCategory, currentMinArea);

		skydiveKompasroosResult.append(resultheader);
		skydiveKompasroosResult.append(nl);
		skydiveKompasroosResult.append(skydiveKompasroosResultAccepted);
		skydiveKompasroosResult.append(nlnl);
		if (skydiveKompasroosResultNeededSizeNotAvailable.length() > 0) {
			skydiveKompasroosResult.append(c
					.getString(R.string.shareresultneededsizenotavailable));
			skydiveKompasroosResult.append(nl);
			skydiveKompasroosResult
					.append(skydiveKompasroosResultNeededSizeNotAvailable);
			skydiveKompasroosResult.append(nlnl);
		}
		if (skydiveKompasroosResultNotAccepted.length() > 0) {
			skydiveKompasroosResult.append(c
					.getString(R.string.shareresultnotaccepted));
			skydiveKompasroosResult.append(nl);
			skydiveKompasroosResult.append(skydiveKompasroosResultNotAccepted);
			skydiveKompasroosResult.append(nlnl);
		}
		skydiveKompasroosResult.append(c
				.getString(R.string.shareresultfooter));

		return skydiveKompasroosResult.toString();
	}

	private void shareResult() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent.putExtra(Intent.EXTRA_TEXT,
				CanopyListActivity.skydiveKompasroosResult(this));
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
		case R.id.menu_sort:
			showDialog(SORT_DIALOG_ID);
			return true;
		case R.id.menu_filter:
			showDialog(FILTER_DIALOG_ID);
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
		i.putExtra(CANOPYIDEXTRA, canopyKey);
		startActivity(i);
	}

	private void insertCanopyHeaderRow(LinearLayout canopyTable, String header) {
		String nl = System.getProperty("line.separator");
		TextView canopyListHeader = new TextView(CanopyListActivity.this);
		canopyListHeader.setText(nl + header);
		canopyListHeader.setTextSize(getResources().getDimension(
				R.dimen.bodyText));

		// create row, and add row to table
		TableRow row = new TableRow(this);
		row.addView(canopyListHeader);
		canopyTable.addView(row);
	}

	/***
	 * Add a canopy row to the canopy table
	 * 
	 * @param canopyTable
	 * @param theCanopy
	 * @param maxCategory
	 */
	private void insertCanopyRow(LinearLayout canopyTable, Canopy theCanopy,
			int maxCategory, int exitWeightInKg) {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View canopyListRow = inflater.inflate(R.layout.canopy_row_layout, null);

		Drawable box = getResources().getDrawable(R.drawable.box);
		LinearLayout hLayout = (LinearLayout) canopyListRow
				.findViewById(R.id.linearLayoutCanopyListRow);
		hLayout.setTag(theCanopy.id.toString());
		if (!theCanopy.isSpecialCatchAllCanopy)
			hLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onCanopyRowClick(v);
				}
			});

		hLayout.setBackgroundDrawable(backgroundDrawableForAcceptance(theCanopy
				.acceptablility(maxCategory, exitWeightInKg)));

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
		if (this.currentSortingMethod != SortingEnum.SORTBYMANUFACTURER)
			tvCanopyDetails.setText(theCanopy.manufacturerName);
		else
			tvCanopyDetails.setText(theCanopy
					.alternativeDetailsText(CanopyListActivity.this));
		// tvCanopyDetails.setBackgroundDrawable(box);

		// if the link won't work, because this is the catch all,
		// don't show the arrow.
		if (theCanopy.isSpecialCatchAllCanopy) {
			TextView tvArrowRight = (TextView) canopyListRow
					.findViewById(R.id.textViewArrowRight);
			tvArrowRight.setText("");
		}

		// create text view and row
		TableRow row = new TableRow(this);
		row.addView(canopyListRow);

		// add row to table
		canopyTable.addView(row);

		// add row to text for results sharing
		// TODO: in case of current sorting by manufacturer, show that first.
		String shareResultLine = theCanopy.name + " - "
				+ theCanopy.manufacturerName
				+ System.getProperty("line.separator");

		switch (theCanopy.acceptablility(currentMaxCategory, currentWeight)) {
		case Canopy.ACCEPTABLE:
			skydiveKompasroosResultAccepted.append(shareResultLine);
			break;
		case Canopy.NEEDEDSIZENOTAVAILABLE:
			skydiveKompasroosResultNeededSizeNotAvailable
					.append(shareResultLine);
			break;
		case Canopy.CATEGORYTOOHIGH:
			skydiveKompasroosResultNotAccepted.append(shareResultLine);
			break;
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SORT_DIALOG_ID:
			return sortDialog();

		case FILTER_DIALOG_ID:
			return filterDialog();

		}
		return null;
	}

	private Dialog sortDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sort_dialog,
				(ViewGroup) findViewById(R.id.root));

		// check the correct radio button in group
		RadioButton radioToCheck = null;
		switch (currentSortingMethod) {
		case SORTBYNAME:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonByName);
			break;
		case SORTBYCATEGORY:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonByCategory);
			break;
		case SORTBYMANUFACTURER:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonByManufacturer);
			break;

		}
		if (radioToCheck != null)
			radioToCheck.setChecked(true);
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// We forcefully dismiss and remove the Dialog, so it
						// cannot be used again (no cached info)
						CanopyListActivity.this.removeDialog(SORT_DIALOG_ID);
					}
				});

		RadioGroup rg = (RadioGroup) layout
				.findViewById(R.id.radioGroupFilterOptions);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int checkButtonId = group.getCheckedRadioButtonId();
				switch (checkButtonId) {
				case R.id.radioButtonByName:
					currentSortingMethod = SortingEnum.SORTBYNAME;
					break;
				case R.id.radioButtonByCategory:
					currentSortingMethod = SortingEnum.SORTBYCATEGORY;
					break;
				case R.id.radioButtonByManufacturer:
					currentSortingMethod = SortingEnum.SORTBYMANUFACTURER;
					break;
				}
				fillCanopyTable(canopyTable, currentSortingMethod,
						currentFilterType);
				CanopyListActivity.this.removeDialog(SORT_DIALOG_ID);
			}
		});

		return builder.create();
	}

	private Dialog filterDialog() {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.filter_dialog,
				(ViewGroup) findViewById(R.id.root));

		// check the correct radio button in group
		RadioButton radioToCheck = null;
		switch (currentFilterType) {
		case ONLYCOMMON:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonCommon);
			break;
		case COMMONAROUNDMAX:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonCommonAround);
			break;
		case ALL:
			radioToCheck = (RadioButton) layout
					.findViewById(R.id.radioButtonAll);
			break;

		}
		if (radioToCheck != null)
			radioToCheck.setChecked(true);

		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setView(layout);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// We forcefully dismiss and remove the Dialog, so it
						// cannot be used again (no cached info)
						CanopyListActivity.this.removeDialog(SORT_DIALOG_ID);
					}
				});

		RadioGroup rg = (RadioGroup) layout
				.findViewById(R.id.radioGroupFilterOptions);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int checkButtonId = group.getCheckedRadioButtonId();
				switch (checkButtonId) {
				case R.id.radioButtonCommon:
					currentFilterType = FilterEnum.ONLYCOMMON;
					break;
				case R.id.radioButtonCommonAround:
					currentFilterType = FilterEnum.COMMONAROUNDMAX;
					break;
				case R.id.radioButtonAll:
					currentFilterType = FilterEnum.ALL;
					break;
				}
				fillCanopyTable(canopyTable, currentSortingMethod,
						currentFilterType);
				CanopyListActivity.this.removeDialog(FILTER_DIALOG_ID);
			}
		});

		return builder.create();
	}

}
