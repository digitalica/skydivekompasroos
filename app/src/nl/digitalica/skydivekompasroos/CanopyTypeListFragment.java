package nl.digitalica.skydivekompasroos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import nl.digitalica.skydivekompasroos.CanopyTypeListFilterDialog.FilterDialogListener;
import nl.digitalica.skydivekompasroos.CanopyTypeListSortDialog.SortDialogListener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CanopyTypeListFragment extends Fragment implements
		FilterDialogListener, SortDialogListener {

	public static String TAG = "canopyTypeList";

	public enum SortingEnum {
		SORTBYNAME, SORTBYMANUFACTURER, SORTBYCATEGORY
	}

	public enum FilterEnum {
		COMMONAROUNDMAX, ONLYCOMMON, ALL
	}

	public List<CanopyType> canopyTypeList;
	public LinearLayout canopyTypeTable;

	public SortingEnum currentSortingMethod;
	public FilterEnum currentFilterType;

	// final int SORT_DIALOG_ID = 1;
	// final int FILTER_DIALOG_ID = 2;

	final static int MILLISINDAY = 1000 * 60 * 60 * 24;

	// static, so it can be statically referenced from onClick...
	static StringBuilder skydiveKompasroosResultAccepted;
	static StringBuilder skydiveKompasroosResultNeededSizeNotAvailable;
	static StringBuilder skydiveKompasroosResultNotAccepted;

	public CanopyTypeListFragment() {
		// empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		// setContentView(R.layout.fragment_canopytypelist);
		View view = inflater.inflate(R.layout.fragment_canopytypelist,
				container, false);

		// if sorting and filter were save over 1 day ago, clear them
		SharedPreferences prefs = getActivity().getSharedPreferences(
				Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
		int sortingFilterTime = prefs.getInt(Skr.SETTING_SORTING_FILTER_DAYNR,
				0);
		int currentTime = (int) (System.currentTimeMillis() / MILLISINDAY);
		if (currentTime - sortingFilterTime > 7) {
			Editor e = prefs.edit();
			e.remove(Skr.SETTING_SORTING);
			e.remove(Skr.SETTING_FILTER_TYPE);
			e.commit();
		}

		// get the saved sorting Method
		int sortingMethodOrdinal = prefs.getInt(Skr.SETTING_SORTING,
				SortingEnum.SORTBYNAME.ordinal());
		this.currentSortingMethod = SortingEnum.values()[sortingMethodOrdinal];

		// get the saved filter cat
		int filterCatdOrdinal = prefs.getInt(Skr.SETTING_FILTER_TYPE,
				FilterEnum.COMMONAROUNDMAX.ordinal());
		this.currentFilterType = FilterEnum.values()[filterCatdOrdinal];

		// add on click handler to share button
		ImageButton shareResultButton = (ImageButton) view
				.findViewById(R.id.buttonShareResult);
		shareResultButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareResult();
			}

		});

		// add on click handler to filter header
		View filterHeader = view.findViewById(R.id.tablelayout_filterheader);
		filterHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// showDialog(FILTER_DIALOG_ID);
				FragmentManager manager = getFragmentManager();
				CanopyTypeListFilterDialog filterDialog = new CanopyTypeListFilterDialog();
				filterDialog.show(manager, "filterDialog");
			}
		});

		// add on click handler to filter button
		ImageButton filterButton = (ImageButton) view
				.findViewById(R.id.buttonEditFilter);
		filterButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				// showDialog(FILTER_DIALOG_ID);
				FragmentManager manager = getFragmentManager();
				CanopyTypeListFilterDialog filterDialog = new CanopyTypeListFilterDialog();
				filterDialog.show(manager, "filterDialog");
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {

		canopyTypeTable = (LinearLayout) getView().findViewById(
				R.id.tablelayout_canopytypelist);

		canopyTypeList = CanopyType.getCanopyTypesInList();

		// TODO: store sorting so it is persistent (?)
		fillCanopyTypeTable(canopyTypeTable, currentSortingMethod,
				currentFilterType);

		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_calculate);

		setHasOptionsMenu(true);
	}

	/***
	 * Fills the canopy type table on screen again based on the given sorting
	 * method
	 * 
	 * @param canopyTypeTable
	 * @param sortingMethod
	 */
	private void fillCanopyTypeTable(LinearLayout canopyTypeTable,
			SortingEnum sortingMethod, FilterEnum filterType) {

		// sort the canopyList on type, name, manufacturer
		Comparator<CanopyType> canopyComparator = null;
		switch (sortingMethod) {
		case SORTBYNAME:
			canopyComparator = new CanopyType.ComparatorByNameManufacturer();
			break;
		case SORTBYCATEGORY:
			canopyComparator = new CanopyType.ComparatorByCategoryName();
			break;
		case SORTBYMANUFACTURER:
			canopyComparator = new CanopyType.ComparatorByManufacturerCategoryName();
			break;
		}
		Collections.sort(canopyTypeList, canopyComparator);

		this.currentSortingMethod = sortingMethod;
		Skr.savePreference(getActivity().getApplicationContext(),
				Skr.SETTING_SORTING, sortingMethod.ordinal());
		this.currentFilterType = filterType;
		Skr.savePreference(getActivity().getApplicationContext(),
				Skr.SETTING_FILTER_TYPE, filterType.ordinal());
		Skr.savePreference(getActivity().getApplicationContext(),
				Skr.SETTING_SORTING_FILTER_DAYNR,
				(int) (System.currentTimeMillis() / MILLISINDAY));
		canopyTypeTable.removeAllViewsInLayout();
		skydiveKompasroosResultAccepted = new StringBuilder();
		skydiveKompasroosResultNeededSizeNotAvailable = new StringBuilder();
		skydiveKompasroosResultNotAccepted = new StringBuilder();

		UUID previousManufacturerId = UUID.randomUUID();
		int previousCat = 9999;
		int shownCount = 0;
		int allCount = 0;
		for (CanopyType theCanopyType : canopyTypeList) {
			allCount++;
			int category = theCanopyType.calculationCategory();
			boolean showThisCanopyType = true;
			// check cat filter
			if (filterType == FilterEnum.ONLYCOMMON)
				if (!theCanopyType.commontype)
					showThisCanopyType = false;
			if (filterType == FilterEnum.COMMONAROUNDMAX)
				if (!theCanopyType.commontype
						|| category < Skr.currentMaxCategory - 1
						|| category > Skr.currentMaxCategory + 1)
					showThisCanopyType = false;
			// show the canopy type (and maybe headerline) if needed
			if (showThisCanopyType) {
				shownCount++;
				if (sortingMethod == SortingEnum.SORTBYMANUFACTURER
						&& !previousManufacturerId
								.equals(theCanopyType.manufacturerId)) {
					insertCanopyTypeHeaderRow(canopyTypeTable,
							theCanopyType.manufacturerName);
					previousManufacturerId = theCanopyType.manufacturerId;
				}
				if (sortingMethod == SortingEnum.SORTBYCATEGORY
						&& previousCat != category) {
					insertCanopyTypeHeaderRow(canopyTypeTable, String.format(
							getString(R.string.canopyListCategoryHeader),
							category));
					previousCat = category;
				}
				insertCanopyTypeRow(canopyTypeTable, theCanopyType,
						Skr.currentMaxCategory, Skr.currentWeight);
			}
		}
		String nl = System.getProperty("line.separator");

		TextView tvFilterText = (TextView) getActivity().findViewById(
				R.id.textview_filtersettings);
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
					Skr.currentMaxCategory) + nl);
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
				Skr.currentTotalJumps, Skr.currentJumpsLast12Months,
				Skr.currentWeight, Skr.currentMaxCategory, Skr.currentMinArea);

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
		skydiveKompasroosResult.append(c.getString(R.string.shareresultfooter));

		return skydiveKompasroosResult.toString();
	}

	private void shareResult() {
		Intent sendIntent = new Intent();
		sendIntent.setAction(Intent.ACTION_SEND);
		sendIntent
				.putExtra(Intent.EXTRA_TEXT, CanopyTypeListFragment
						.skydiveKompasroosResult(getActivity()
								.getApplicationContext()));
		sendIntent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.shareresultsubject));
		sendIntent.putExtra(Intent.EXTRA_TITLE,
				getString(R.string.shareresultsubject));
		sendIntent.setType("text/plain");
		startActivity(sendIntent);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_canopylist, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_shareResult:
			shareResult();
			return true;
		case R.id.menu_sort:
			// showDialog(SORT_DIALOG_ID);
			FragmentManager manager1 = getFragmentManager();
			CanopyTypeListSortDialog sortDialog = new CanopyTypeListSortDialog();
			sortDialog.show(manager1, "sortDialog");
			return true;
		case R.id.menu_filter:
			// showDialog(FILTER_DIALOG_ID);
			FragmentManager manager2 = getFragmentManager();
			CanopyTypeListFilterDialog filterDialog = new CanopyTypeListFilterDialog();
			filterDialog.show(manager2, "filterDialog");
			return true;
		case R.id.menu_about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/***
	 * Generic click handle to for clicks on a canopy type row the tag is used
	 * to decide what canopy details to show.
	 * 
	 * @param v
	 */
	private void onCanopyTypeRowClick(View v) {
		String canopyKey = v.getTag().toString();
		Intent i = new Intent(getActivity().getBaseContext(),
				CanopyDetailsActivity.class);
		// TODO: remove extras as they will be in global vars...
		i.putExtra(Skr.CANOPYIDEXTRA, canopyKey);
		startActivity(i);
		// TODO: animate fracment transition
		// overridePendingTransition(R.anim.right_in, R.anim.left_out);
	}

	private void insertCanopyTypeHeaderRow(LinearLayout canopyTable,
			String header) {
		String nl = System.getProperty("line.separator");
		TextView canopyListHeader = new TextView(getActivity());
		canopyListHeader.setText(nl + header);
		canopyListHeader.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
				.getDimension(R.dimen.canopylistHeader));

		// create row, and add row to table
		TableRow row = new TableRow(getActivity());
		row.addView(canopyListHeader);
		canopyTable.addView(row);
	}

	/***
	 * Add a canopy row to the canopy table
	 * 
	 * @param canopyTypeTable
	 * @param theCanopyType
	 * @param maxCategory
	 */
	private void insertCanopyTypeRow(LinearLayout canopyTypeTable,
			CanopyType theCanopyType, int maxCategory, int exitWeightInKg) {

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		View canopyTypeListRow = inflater.inflate(
				R.layout.canopy_type_row_layout, null);

		// Drawable box = getResources().getDrawable(R.drawable.box);
		LinearLayout hLayout = (LinearLayout) canopyTypeListRow
				.findViewById(R.id.linearLayoutCanopyListRow);
		hLayout.setTag(theCanopyType.id.toString());
		if (!theCanopyType.isSpecialCatchAllCanopy)
			hLayout.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					onCanopyTypeRowClick(v);
				}
			});

		hLayout.setBackgroundDrawable(Skr.backgroundForAcceptance(getActivity()
				.getApplicationContext(), theCanopyType.acceptablility(
				maxCategory, exitWeightInKg)));

		// hLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));

		TextView tvCategory = (TextView) canopyTypeListRow
				.findViewById(R.id.textViewCanopyListRowCategory);
		// tvCategory.setBackgroundDrawable(box);
		tvCategory.setText(theCanopyType.displayCategory());

		TextView tvCanopyName = (TextView) canopyTypeListRow
				.findViewById(R.id.textViewCanopyListRowName);
		// tvCanopyName.setBackgroundDrawable(box);
		tvCanopyName.setText(theCanopyType.name);

		// TODO: add more details like cells and remarks here
		TextView tvCanopyDetails = (TextView) canopyTypeListRow
				.findViewById(R.id.textViewCanopyListRowDetails);
		if (this.currentSortingMethod != SortingEnum.SORTBYMANUFACTURER)
			tvCanopyDetails.setText(theCanopyType.manufacturerName);
		else
			tvCanopyDetails.setText(theCanopyType
					.alternativeDetailsText(getActivity()
							.getApplicationContext()));
		// tvCanopyDetails.setBackgroundDrawable(box);

		// if the link won't work, because this is the catch all,
		// don't show the arrow.
		if (theCanopyType.isSpecialCatchAllCanopy) {
			TextView tvArrowRight = (TextView) canopyTypeListRow
					.findViewById(R.id.textViewArrowRight);
			tvArrowRight.setText("");
		}

		// create text view and row
		TableRow row = new TableRow(getActivity().getApplicationContext());
		row.addView(canopyTypeListRow);

		// add row to table
		canopyTypeTable.addView(row);

		// add row to text for results sharing
		// TODO: in case of current sorting by manufacturer, show that first.
		String shareResultLine = theCanopyType.name + " - "
				+ theCanopyType.manufacturerName
				+ System.getProperty("line.separator");

		switch (theCanopyType.acceptablility(Skr.currentMaxCategory,
				Skr.currentWeight)) {
		case ACCEPTABLE:
			skydiveKompasroosResultAccepted.append(shareResultLine);
			break;
		case NEEDEDSIZENOTAVAILABLE:
			skydiveKompasroosResultNeededSizeNotAvailable
					.append(shareResultLine);
			break;
		case CATEGORYTOOHIGH:
			skydiveKompasroosResultNotAccepted.append(shareResultLine);
			break;
		}

	}

	public FilterEnum getFilterType() {
		return currentFilterType;
	}

	public void setFilterType(FilterEnum filterType) {
		currentFilterType = filterType;
		fillCanopyTypeTable(canopyTypeTable, currentSortingMethod,
				currentFilterType);
	}

	public SortingEnum getSorting() {
		return currentSortingMethod;
	}

	public void setSorting(SortingEnum sorting) {
		currentSortingMethod = sorting;
		fillCanopyTypeTable(canopyTypeTable, currentSortingMethod,
				currentFilterType);
	}

	// @Override
	// protected Dialog onCreateDialog(int id) {
	// switch (id) {
	// case SORT_DIALOG_ID:
	// return sortDialog();
	//
	// case FILTER_DIALOG_ID:
	// return filterDialog();
	//
	// }
	// return null;
	// }

	// @Override
	// protected void onPrepareDialog(final int id, final Dialog dialog) {
	// switch (id) {
	// case SORT_DIALOG_ID:
	// case FILTER_DIALOG_ID:
	// AlertDialog alert = (AlertDialog) dialog;
	// Button cancel = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
	// if (cancel != null)
	// cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	// .getDimension(R.dimen.bodyText));
	// Button ok = alert.getButton(AlertDialog.BUTTON_POSITIVE);
	// if (ok != null)
	// ok.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	// .getDimension(R.dimen.bodyText));
	//
	// }
	// }

	// @Override
	// public void onBackPressed() {
	// super.onBackPressed();
	// overridePendingTransition(R.anim.left_in, R.anim.right_out);
	// }

}
