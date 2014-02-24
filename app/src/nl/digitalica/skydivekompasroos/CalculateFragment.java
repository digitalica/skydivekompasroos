package nl.digitalica.skydivekompasroos;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import nl.digitalica.skydivekompasroos.CalculateResetDialog.ResetDialogListener;
import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * @author robbert
 * 
 */
public class CalculateFragment extends Fragment implements ResetDialogListener

{

	// min & max weight in kg
	public final static int WEIGHT_MIN = 50;
	public final static int WEIGHT_MAX = 140;
	public final static int WEIGHT_DEFAULT = 100;

	// max for total jumps
	public final static int TOTALJUMPS_MAX = 1100;
	public final static int TOTALJUMPS_LASTGROUP = 1000;
	public final static int TOTALJUMPS_DEFAULT = 100;

	// max for total jumps
	public final static int JUMPS_LAST_12_MONTHS_MAX = 125;
	public final static int JUMPS_LAST_12_MONTHS_LASTGROUP = 100;
	public final static int JUMPS_LAST_12_MONTHS_DEFAULT = 25;

	// dialog ID's
	// final static int SAVE_DIALOG_ID = 1;
	// final static int RESET_DIALOG_ID = 2;
	final static int TOTAL_JUMPS_DIALOG_ID = 3;
	final static int JUMPS_LAST_12_MONTHS_DIALOG_ID = 4;
	final static int WEIGHT_DIALOG_ID = 5;

	public final static String TAG = "calculateFragment";

	public CalculateFragment() {
		// empty constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_calculate, container,
				false);

		// if compile date over 1 year ago, show warning text
		TextView tvWarning = (TextView) view.findViewById(R.id.textViewWarning);
		String warning = "";
		try {
			long compilationDateTime = Skr.getCompileDateTime(getActivity()
					.getApplicationContext());
			Calendar cal = Calendar.getInstance();
			long now = cal.getTime().getTime();
			long maxDiff = 1000L * 60L * 60L * 24L * 365L; // 1 year
			// maxDiff = 1000 * 60 * 5; // 5 mins (for testing)
			if (now - compilationDateTime > maxDiff)
				warning = getString(R.string.calculationOvertimeWarning);
		} catch (Exception e) {
			warning = getString(R.string.calculationOvertimeUnknownWarning);
		}
		tvWarning.setText(warning);

		// set click listener for canopy list button
		ImageButton canopyListButton = (ImageButton) view
				.findViewById(R.id.buttonShowCanopyList);
		canopyListButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getActivity().getBaseContext(),
						CanopyTypeListActivity.class));
				getActivity().overridePendingTransition(R.anim.right_in,
						R.anim.left_out);
			}
		});

		// set click listener for about button
		ImageButton aboutButton = (ImageButton) view
				.findViewById(R.id.buttonAbout);
		aboutButton.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getActivity().getBaseContext(),
						AboutActivity.class));
				getActivity().overridePendingTransition(R.anim.left_in,
						R.anim.right_out);
			}
		});

		// add click listener for allowed header
		View filterHeader = view.findViewById(R.id.tablelayout_filterheader);
		filterHeader.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(getActivity().getBaseContext(),
						CanopyTypeListActivity.class));
			}
		});

		// set click listener for specific canopy add
		Button addSpecificCanopy = (Button) view
				.findViewById(R.id.buttonAddSpecificCanopy);
		addSpecificCanopy.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(getActivity().getBaseContext(),
						SpecificCanopyEdit.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Skr.SPECIFICCANOPYID_KEY, 0);
				intent.putExtras(bundle);
				startActivity(intent);
				getActivity().overridePendingTransition(R.anim.explode_in,
						R.anim.none);
			}
		});

		// Just for testing the canopy list
		// canopyListButton.performClick();

		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// initialize seek bars and calculated texts
		initSeekBars();
		fillSpecificCanopyTable();
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_calculate);

		setHasOptionsMenu(true);
	}

	@Override
	public void onStart() {
		super.onStart();
		fillSpecificCanopyTable();
	}

	@Override
	public void onResume() {
		super.onResume();
		initSeekBarTextsAndCalculate();
		updateSpecificCanopyTable();
	}

	private void fillSpecificCanopyTable() {
		TableLayout scTable = (TableLayout) getView().findViewById(
				R.id.tableSpecificCanopies);
		scTable.removeAllViews();
		insertCanopyHeaderRow(scTable);
		List<SpecificCanopy> scList = SpecificCanopy
				.getSpecificCanopiesInList(getActivity()
						.getApplicationContext());
		HashMap<UUID, CanopyType> canopyTypes = CanopyType
				.getCanopyTypeHash(getActivity().getApplicationContext());
		for (SpecificCanopy theCanopy : scList) {
			CanopyType ct = canopyTypes.get(theCanopy.typeId);
			insertSpecificCanopyRow(scTable, theCanopy, ct.name,
					ct.calculationCategory());
		}

		// enable or disable button if needed
		Button addSpecificCanopy = (Button) getView().findViewById(
				R.id.buttonAddSpecificCanopy);
		if (scList.size() >= SpecificCanopy.MAXSPECIFICCANOPIES)
			addSpecificCanopy.setEnabled(false);
		else
			addSpecificCanopy.setEnabled(true);
	}

	/**
	 * Update the already drawn specific canopy table, based on the data in the
	 * table itself.
	 */
	private void updateSpecificCanopyTable() {
		TableLayout scTable = (TableLayout) getView().findViewById(
				R.id.tableSpecificCanopies);
		for (int i = 1; i < scTable.getChildCount(); i++) {
			TableRow canopyListRow = (TableRow) scTable.getChildAt(i);
			TextView tvSize = (TextView) canopyListRow
					.findViewById(R.id.textViewSpecificSize);
			TextView tvType = (TextView) canopyListRow
					.findViewById(R.id.textViewSpecificType);
			TextView tvRemarks = (TextView) canopyListRow
					.findViewById(R.id.textViewSpecificRemarks);
			TextView tvWingLoad = (TextView) canopyListRow
					.findViewById(R.id.textViewSpecificWingload);

			int tagAll = (Integer) tvWingLoad.getTag();
			int typeCategory = tagAll % 10;
			int size = (tagAll - typeCategory) / 10;

			double wingload = Calculation.wingLoad(size, Skr.currentWeight);
			tvWingLoad.setText(String.format("%.2f", wingload));

			AcceptabilityEnum acc = SpecificCanopy.acceptablility(
					Skr.currentMaxCategory, typeCategory, size,
					Skr.currentWeight);
			// We need different drawables for each column as the widths are
			// different
			Context ctx = getActivity().getApplicationContext();
			Drawable backgroundCol1 = Skr.backgroundForAcceptance(ctx, acc);
			Drawable backgroundCol2 = Skr.backgroundForAcceptance(ctx, acc);
			Drawable backgroundCol3 = Skr.backgroundForAcceptance(ctx, acc);
			Drawable backgroundCol4 = Skr.backgroundForAcceptance(ctx, acc);

			tvSize.setBackgroundDrawable(backgroundCol1);
			tvType.setBackgroundDrawable(backgroundCol2);
			tvRemarks.setBackgroundDrawable(backgroundCol3);
			tvWingLoad.setBackgroundDrawable(backgroundCol4);
		}
	}

	private void insertCanopyHeaderRow(TableLayout scTable) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View canopyListRow = inflater.inflate(
				R.layout.specific_canopy_row_layout, null);

		TextView tvSize = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificSize);
		TextView tvType = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificType);
		TextView tvRemarks = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificRemarks);
		TextView tvWingload = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificWingload);

		tvSize.setText(getString(R.string.specificCanopyHeaderSize));
		tvType.setText(getString(R.string.specificCanopyHeaderType));
		tvRemarks.setText(getString(R.string.specificCanopyHeaderRemarks));
		tvWingload.setText(getString(R.string.specificCanopyHeaderWingLoad));

		scTable.addView(canopyListRow);
	}

	private void insertSpecificCanopyRow(TableLayout scTable,
			SpecificCanopy theCanopy, String typeName, int typeCategory) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View canopyListRow = inflater.inflate(
				R.layout.specific_canopy_row_layout, null);

		TextView size = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificSize);
		TextView type = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificType);
		TextView remarks = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificRemarks);
		TextView wingload = (TextView) canopyListRow
				.findViewById(R.id.textViewSpecificWingload);

		size.setText(Integer.toString(theCanopy.size));
		type.setText(typeName);
		remarks.setText(theCanopy.remarks);
		wingload.setTag(theCanopy.size * 10 + typeCategory);

		// set click listener for specific canopy edit

		canopyListRow.setTag(theCanopy.id);
		canopyListRow.setOnClickListener(new View.OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent(getActivity().getBaseContext(),
						SpecificCanopyEdit.class);
				Bundle bundle = new Bundle();
				bundle.putInt(Skr.SPECIFICCANOPYID_KEY, (Integer) v.getTag());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		// add it to the table
		scTable.addView(canopyListRow);
	}

	/***
	 * Set three seekbars. This will automatically trigger calculate calls
	 * through the OnProgressChanged of the seekbars.
	 * 
	 * @param weight
	 * @param totalJumps
	 * @param jumpsLast12Months
	 */
	public void setSeekBars(int weight, int totalJumps, int jumpsLast12Months) {
		((SeekBar) getView().findViewById(R.id.seekBarWeight))
				.setProgress(weight);
		((SeekBar) getView().findViewById(R.id.seekBarTotalJumps))
				.setProgress(totalJumps);
		((SeekBar) getView().findViewById(R.id.seekBarJumpsLast12Months))
				.setProgress(jumpsLast12Months);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.activity_calculate, menu);
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		FragmentManager manager = getFragmentManager();
		switch (item.getItemId()) {
		case R.id.menu_reset:
			// getActivity().showDialog(RESET_DIALOG_ID);
			CalculateResetDialog resetDialog = new CalculateResetDialog();
			resetDialog.show(manager, "resetDialog");
			return true;
		case R.id.menu_save:
			// getActivity().showDialog(SAVE_DIALOG_ID);
			CalculateSaveDialog saveDialog = new CalculateSaveDialog();
			saveDialog.show(manager, "saveDialog");
			return true;
		case R.id.menu_about:
			startActivity(new Intent(getActivity(), AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	// @Override
	// protected Dialog onCreateDialog(int id) {
	// switch (id) {
	// case SAVE_DIALOG_ID:
	// return saveDialog();
	// case RESET_DIALOG_ID:
	// return resetDialog();
	// case TOTAL_JUMPS_DIALOG_ID:
	// return totalJumpsDialog();
	// case JUMPS_LAST_12_MONTHS_DIALOG_ID:
	// return jumpsLast12MonthsDialog();
	// case WEIGHT_DIALOG_ID:
	// return weightDialog();
	// }
	// return null;
	// }

	// @Override
	// protected void onPrepareDialog(final int id, final Dialog dialog) {
	// switch (id) {
	// case SAVE_DIALOG_ID:
	// case RESET_DIALOG_ID:
	// case TOTAL_JUMPS_DIALOG_ID:
	// case JUMPS_LAST_12_MONTHS_DIALOG_ID:
	// case WEIGHT_DIALOG_ID:
	// AlertDialog alert = (AlertDialog) dialog;
	// Button cancel = alert.getButton(AlertDialog.BUTTON_NEGATIVE);
	// if (cancel != null)
	// cancel.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	// .getDimension(R.dimen.bodyText));
	// Button ok = alert.getButton(AlertDialog.BUTTON_POSITIVE);
	// if (ok != null)
	// ok.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	// .getDimension(R.dimen.bodyText));
	// TextView message = (TextView) dialog
	// .findViewById(android.R.id.message);
	// if (message != null)
	// message.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources()
	// .getDimension(R.dimen.bodyText));
	//
	// }
	// }

	/***
	 * Initialize the seekbar listeners
	 */
	private void initSeekBars() {
		// weight seek bar
		View v1 = getView();
		View v2 = v1.findViewById(R.id.seekBarWeight);
		SeekBar sbWeight = (SeekBar) v2;
		sbWeight.setMax(WEIGHT_MAX - WEIGHT_MIN);
		sbWeight.setOnSeekBarChangeListener(seekBarChangeListenerWeight);
		setPlusMinButtonListeners(sbWeight, R.id.buttonWeightMin,
				R.id.buttonWeightPlus, WEIGHT_DIALOG_ID);

		// total jumps seek bar
		SeekBar sbTotalJumps = (SeekBar) getView().findViewById(
				R.id.seekBarTotalJumps);
		sbTotalJumps.setMax(TOTALJUMPS_MAX);
		sbTotalJumps
				.setOnSeekBarChangeListener(seekBarChangeListenerTotalJumps);
		setPlusMinButtonListeners(sbWeight, R.id.buttonTotalJumpsMin,
				R.id.buttonTotalJumpsPlus, TOTAL_JUMPS_DIALOG_ID);

		// jumps last 12 months seek bar
		SeekBar sbJumpsLast12Months = (SeekBar) getView().findViewById(
				R.id.seekBarJumpsLast12Months);
		sbJumpsLast12Months.setMax(JUMPS_LAST_12_MONTHS_MAX);
		sbJumpsLast12Months
				.setOnSeekBarChangeListener(seekBarChangeListenerJumpsLast12Months);
		setPlusMinButtonListeners(sbWeight, R.id.buttonJumpLast12MonthsMin,
				R.id.buttonJumpLast12MonthsPlus, JUMPS_LAST_12_MONTHS_DIALOG_ID);
	}

	/***
	 * Initialize the seekbars texts
	 */
	private void initSeekBarTextsAndCalculate() {
		SharedPreferences prefs = getActivity().getSharedPreferences(
				Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
		// weight seek bar
		SeekBar sbWeight = (SeekBar) getView().findViewById(R.id.seekBarWeight);
		int weightInKg = prefs.getInt(Skr.SETTING_WEIGHT, WEIGHT_DEFAULT);
		sbWeight.setProgress(weightInKg - WEIGHT_MIN);

		// total jumps seek bar
		SeekBar sbTotalJumps = (SeekBar) getView().findViewById(
				R.id.seekBarTotalJumps);
		int totalJumps = prefs.getInt(Skr.SETTING_TOTAL_JUMPS,
				TOTALJUMPS_DEFAULT);
		sbTotalJumps.setProgress(totalJumps);

		// jumps last 12 months seek bar
		SeekBar sbJumpsLast12Months = (SeekBar) getView().findViewById(
				R.id.seekBarJumpsLast12Months);
		int jumpsLast12Months = prefs.getInt(Skr.SETTING_JUMPS_LAST_12_MONTHS,
				JUMPS_LAST_12_MONTHS_DEFAULT);
		sbJumpsLast12Months.setProgress(jumpsLast12Months);

		// now calculate to set all texts
		calculate();
	}

	/***
	 * Add click listeners to the plus and min buttons at the left and right of
	 * a seekbar
	 */
	private void setPlusMinButtonListeners(SeekBar sb, int minButtonId,
			int plusButtonId, final int longClickDialogId) {
		// get the buttons based on the give id's
		Button minButton = (Button) getView().findViewById(minButtonId);
		Button plusButton = (Button) getView().findViewById(plusButtonId);
		// add click listener to the min button
		minButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// find matching seekbar
				ViewGroup hList = (ViewGroup) v.getParent();
				if (hList.getChildCount() != 3)
					Log.e(Skr.LOG_TAG,
							"Incorrect number of children in seekbargroup");
				SeekBar sb = (SeekBar) hList.getChildAt(1);
				int progress = sb.getProgress();
				if (progress > 0)
					sb.setProgress(progress - 1);
			}
		});
		// add click listener to the plus button
		plusButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				// find matching seekbar
				ViewGroup hList = (ViewGroup) v.getParent();
				if (hList.getChildCount() != 3)
					Log.e(Skr.LOG_TAG,
							"Incorrect number of children in seekbargroup");
				SeekBar sb = (SeekBar) hList.getChildAt(1);
				int progress = sb.getProgress();
				if (progress < sb.getMax())
					sb.setProgress(progress + 1);
			}
		});
		// add long click listeners
		minButton.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				getActivity().showDialog(longClickDialogId);
				openSliderDialog(longClickDialogId);

				return true;
			}
		});
		plusButton.setOnLongClickListener(new View.OnLongClickListener() {

			public boolean onLongClick(View v) {
				getActivity().showDialog(longClickDialogId);
				return true;
			}
		});
	}

	private void openSliderDialog(int dialogId) {
		FragmentManager manager = getFragmentManager();

		switch (dialogId) {
		case WEIGHT_DIALOG_ID:
			CalculateResetDialog resetDialog = new CalculateResetDialog();
			resetDialog.show(manager, "resetDialog");
			break;
		case TOTAL_JUMPS_DIALOG_ID:
			CalculateTotalJumpsDialog totalJumpsDialog = new CalculateTotalJumpsDialog();
			totalJumpsDialog.show(manager, "totalJumpsDialog");
			break;
		case JUMPS_LAST_12_MONTHS_DIALOG_ID:
			CalculateJumpsLast12MonthsDialog jumpsLast12MonthsDialog = new CalculateJumpsLast12MonthsDialog();
			jumpsLast12MonthsDialog.show(manager, "jumpsLast12MonthsDialog");
			break;
		}
	}

	/***
	 * The seekbar change listener for the exit weight
	 */
	private OnSeekBarChangeListener seekBarChangeListenerWeight = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			int weightInKg = progress + WEIGHT_MIN;
			Skr.savePreference(getActivity().getApplicationContext(),
					Skr.SETTING_WEIGHT, weightInKg);
			Skr.currentWeight = weightInKg;
			setWeightSettingText(weightInKg);
			calculate();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// No action required
		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// No action required
		}

	};

	/***
	 * Listener for changes on the total jumps seekbar. Triggers a calculate
	 * call, that updates texts where needed.
	 */
	private OnSeekBarChangeListener seekBarChangeListenerTotalJumps = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Skr.savePreference(getActivity().getApplicationContext(),
					Skr.SETTING_TOTAL_JUMPS, progress);
			Skr.currentTotalJumps = progress;
			setTotalJumpsSettingText(progress);
			// check to see if jumps in last 12 months in not higher
			SeekBar sbJumpsLast12Months = (SeekBar) getView().findViewById(
					R.id.seekBarJumpsLast12Months);
			int jumpsLast12Months = sbJumpsLast12Months.getProgress();
			if (jumpsLast12Months > progress)
				sbJumpsLast12Months.setProgress(progress);
			calculate();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	/***
	 * Listener for changes on the jumps in last 12 months seekbar. Triggers a
	 * calculate call, that updates texts where needed.
	 */
	private OnSeekBarChangeListener seekBarChangeListenerJumpsLast12Months = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			Skr.savePreference(getActivity().getApplicationContext(),
					Skr.SETTING_JUMPS_LAST_12_MONTHS, progress);
			Skr.currentJumpsLast12Months = progress;
			setJumpsLast12MonthsSettingText(progress);
			// check to see if jumps in last 12 months in not higher
			SeekBar sbTotalJumps = (SeekBar) getView().findViewById(
					R.id.seekBarTotalJumps);
			int totalJumps = sbTotalJumps.getProgress();
			if (totalJumps < progress)
				sbTotalJumps.setProgress(progress);
			calculate();
		}

		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * Fills the wingloadtable. As the table is allways six positings, its
	 * starting point depends on the current category and weight.
	 * 
	 * @param weightInKg
	 *            TODO: split in fill (first row) & update (second row & colors)
	 */
	private void fillWingloadTable(int weightInKg) {

		final int[] WLTBL = new int[] { 120, 135, 150, 170, 190, 210, 230 };

		// fill the wingload table
		int column = 0;
		TextView area1 = (TextView) getView().findViewById(R.id.textViewArea1);
		TextView wingLoad1 = (TextView) getView().findViewById(
				R.id.textViewWingLoad1);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area1, wingLoad1);
		TextView area2 = (TextView) getView().findViewById(R.id.textViewArea2);
		TextView wingLoad2 = (TextView) getView().findViewById(
				R.id.textViewWingLoad2);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area2, wingLoad2);
		TextView area3 = (TextView) getView().findViewById(R.id.textViewArea3);
		TextView wingLoad3 = (TextView) getView().findViewById(
				R.id.textViewWingLoad3);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area3, wingLoad3);
		TextView area4 = (TextView) getView().findViewById(R.id.textViewArea4);
		TextView wingLoad4 = (TextView) getView().findViewById(
				R.id.textViewWingLoad4);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area4, wingLoad4);
		TextView area5 = (TextView) getView().findViewById(R.id.textViewArea5);
		TextView wingLoad5 = (TextView) getView().findViewById(
				R.id.textViewWingLoad5);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area5, wingLoad5);
		TextView area6 = (TextView) getView().findViewById(R.id.textViewArea6);
		TextView wingLoad6 = (TextView) getView().findViewById(
				R.id.textViewWingLoad6);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area6, wingLoad6);
		TextView area7 = (TextView) getView().findViewById(R.id.textViewArea7);
		TextView wingLoad7 = (TextView) getView().findViewById(
				R.id.textViewWingLoad7);
		fillWingLoadTableColumn(WLTBL[column++], weightInKg, area7, wingLoad7);
	}

	/***
	 * Fills a single column in the wing load table
	 * 
	 * @param area
	 * @param weightInKg
	 * @param tvArea
	 * @param tvWingLoad
	 */
	private void fillWingLoadTableColumn(int area, int weightInKg,
			TextView tvArea, TextView tvWingLoad) {
		double wingload = Calculation.wingLoad(area, weightInKg);

		Drawable backgroundRed = getResources().getDrawable(
				R.drawable.canopycategorytoohigh);
		Drawable backgroundOrange = getResources().getDrawable(
				R.drawable.canopyneededsizenotavailable);
		Drawable backgroundGreen = getResources().getDrawable(
				R.drawable.canopyacceptable);

		int areaAllowedOnCat = Calculation
				.minAreaBasedOnCategory(Skr.currentMaxCategory);
		boolean areaOrWingloadOutOfRange = area < Calculation.minArea(
				Skr.currentMaxCategory, weightInKg);
		tvArea.setText(String.format("%d", area));
		if (area < areaAllowedOnCat)
			tvArea.setBackgroundDrawable(backgroundRed);
		else if (areaOrWingloadOutOfRange)
			tvArea.setBackgroundDrawable(backgroundOrange);
		else
			tvArea.setBackgroundDrawable(backgroundGreen);

		double maxWinloadAllowed = Calculation
				.maxWingLoadBasedOnCategory(Skr.currentMaxCategory);
		tvWingLoad.setText(String.format("%.2f", wingload));
		if (wingload > maxWinloadAllowed)
			tvWingLoad.setBackgroundDrawable(backgroundRed);
		else if (areaOrWingloadOutOfRange)
			tvWingLoad.setBackgroundDrawable(backgroundOrange);
		else
			tvWingLoad.setBackgroundDrawable(backgroundGreen);
	}

	private void setTotalJumpsSettingText(int totalJumps) {
		TextView tvTotalJumps = (TextView) getView().findViewById(
				R.id.textViewTotalJumpsLabel);
		String totalJumpsLabel = getString(R.string.calculationTotalJumpsLabel);
		String totalJumpsFormat = getString(R.string.calculationTotalJumpsSetting);
		String orMoreText = "";
		if (totalJumps > TOTALJUMPS_LASTGROUP)
			orMoreText = getString(R.string.ormore);
		tvTotalJumps.setText(totalJumpsLabel
				+ String.format(totalJumpsFormat, totalJumps, orMoreText));
	}

	private void setJumpsLast12MonthsSettingText(int jumps) {
		TextView tvJumpsLast12Months = (TextView) getView().findViewById(
				R.id.textViewJumpsLast12MonthsLabel);
		String jumpsLast12MonthsLabel = getString(R.string.calculationJumpsLast12MonthsLabel);
		String jumpsLast12MonthsFormat = getString(R.string.calculationJumpsLast12MonthsSetting);
		String orMoreText = "";
		if (jumps > JUMPS_LAST_12_MONTHS_LASTGROUP)
			orMoreText = getString(R.string.ormore);
		tvJumpsLast12Months.setText(jumpsLast12MonthsLabel
				+ String.format(jumpsLast12MonthsFormat, jumps, orMoreText));
	}

	private void setWeightSettingText(int weightInKg) {
		TextView tvWeight = (TextView) getView().findViewById(
				R.id.textViewWeightLabel);
		String weightLabel = getString(R.string.calculationWeightLabel);
		String weightFormat = getString(R.string.calculationWeightSetting);
		tvWeight.setText(weightLabel
				+ String.format(weightFormat, weightInKg,
						Calculation.kgToLbs(weightInKg)));
		fillWingloadTable(weightInKg);
	}

	/**
	 * Calculate the current category, based on weight, total jumps and jumps
	 * last year.
	 */
	private void calculate() {
		// get weight and set text
		SeekBar sbWeight = (SeekBar) getView().findViewById(R.id.seekBarWeight);
		int weightInKg = sbWeight.getProgress() + WEIGHT_MIN;

		SeekBar sbTotalJumps = (SeekBar) getView().findViewById(
				R.id.seekBarTotalJumps);
		int totalJumps = sbTotalJumps.getProgress();

		SeekBar sbJumpsLast12Months = (SeekBar) getView().findViewById(
				R.id.seekBarJumpsLast12Months);
		int jumpsLast12Months = sbJumpsLast12Months.getProgress();

		int jumperCategory = Calculation.jumperCategory(totalJumps,
				jumpsLast12Months);

		// now decide on minArea and maxWingload
		int minArea = Calculation.minArea(jumperCategory, weightInKg);

		// only update screen if there actually is a change, so seek bars
		// respond quickly
		if (Skr.currentMaxCategory != jumperCategory
				|| Skr.currentMinArea != minArea) {
			TextView tvJumperCategory = (TextView) getView().findViewById(
					R.id.textViewJumperCategory);
			String jumperCatFormat = getString(R.string.categorySetting);
			tvJumperCategory.setText(String.format(jumperCatFormat,
					jumperCategory));

			TextView tvJumperCategoryDescription = (TextView) getView()
					.findViewById(R.id.textViewJumperCategoryDescription);
			String jumperCatDescriptionFormat = getString(R.string.categorySettingDescription);
			String jumperCategories[] = getResources().getStringArray(
					R.array.jumperCategories);
			tvJumperCategoryDescription.setText(String.format(
					jumperCatDescriptionFormat,
					jumperCategories[jumperCategory]));

			TextView tvCanopyCategory = (TextView) getView().findViewById(
					R.id.textViewCanopyCategoryText);
			String canopyCatFormat;
			canopyCatFormat = String.format(
					getString(R.string.calculationCanopyCategory),
					jumperCategory);
			tvCanopyCategory.setText(canopyCatFormat);

			TextView tvCanopyMinArea = (TextView) getView().findViewById(
					R.id.textViewCanopyMinAreaText);
			int minAreaBasedOnCategory = Calculation
					.minAreaBasedOnCategory(jumperCategory);
			String minAreaText;
			// TODO: this string should be taken from canopy class (or method in
			// calculation)
			switch (minAreaBasedOnCategory) {
			case 0:
				minAreaText = getString(R.string.calculationCanopyMinAreaAny);
				break;
			default:
				minAreaText = String.format(
						getString(R.string.calculationCanopyMinArea),
						minAreaBasedOnCategory);
				break;
			}
			tvCanopyMinArea.setText(minAreaText);

			TextView tvCanopyMaxWingLoad = (TextView) getView().findViewById(
					R.id.textViewCanopyMaxWingLoadText);
			double maxWingLoad = Calculation
					.maxWingLoadBasedOnCategory(jumperCategory);
			String maxWingLoadText;
			if (maxWingLoad > 10)
				maxWingLoadText = getString(R.string.calculationCanopyMaxWingLoadAny);
			else
				maxWingLoadText = String.format(
						getString(R.string.calculationCanopyMaxWingLoad),
						maxWingLoad);
			tvCanopyMaxWingLoad.setText(maxWingLoadText);

			TextView tvCanopyAdvise = (TextView) getView().findViewById(
					R.id.textViewCanopyAdvise);
			// TODO: this string should be taken from a jumper class (or method
			// in calculation). Also in details screen of course.
			if (minArea == 0)
				tvCanopyAdvise.setText(String.format(
						getString(R.string.canopyAdvise), jumperCategory));
			else
				tvCanopyAdvise.setText(String.format(
						getString(R.string.canopyAdviseWithMinSize),
						jumperCategory, minArea));

			// save globally, to pass on in buttonClick
			Skr.currentMaxCategory = jumperCategory;
			Skr.currentMinArea = minArea;

			fillWingloadTable(weightInKg);
			updateSpecificCanopyTable();
		}
	}

	public void onFinishResetDialog(int weight, int totalJumps,
			int jumpsLast12Months) {
		// TODO Auto-generated method stub
		setSeekBars(weight, totalJumps, jumpsLast12Months);
	}
}
