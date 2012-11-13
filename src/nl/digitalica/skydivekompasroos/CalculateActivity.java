package nl.digitalica.skydivekompasroos;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

/**
 * @author robbert
 * 
 */
public class CalculateActivity extends Activity {

	final static String KOMPASROOSPREFS = "Skydive_Kompasroos_Preferences";

	final static String LOG_TAG = "SkydiveKompasRoos";

	// namen voor de verschillende settings
	final static String SETTING_WEIGHT = "Weight"; // string
	final static String SETTING_TOTAL_JUMPS = "TotalJumps"; // int
	final static String SETTING_JUMPS_LAST_12_MONTHS = "JumpsLast12Months"; // int

	// min & max weight in kg
	final static int WEIGHT_MIN = 80;
	final static int WEIGHT_MAX = 140;
	final static int WEIGHT_DEFAULT = 100;

	// max for total jumps
	final static int TOTALJUMPS_MAX = 1100;
	final static int TOTALJUMPS_LASTGROUP = 1000;
	final static int TOTALJUMPS_DEFAULT = 100;

	// max for total jumps
	final static int JUMPS_LAST_12_MONTHS_MAX = 125;
	final static int JUMPS_LAST_12_MONTHS_LASTGROUP = 100;
	final static int JUMPS_LAST_12_MONTHS_DEFAULT = 25;

	// wingload table
	final static int WINGLOAD_FIRST_COL = 130;
	final static int WINGLOAD_STEP = 20;

	// class variables
	SharedPreferences prefs;

	int category = 0;
	int minArea = 999;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculate);
		prefs = getSharedPreferences(KOMPASROOSPREFS, Context.MODE_PRIVATE);

		initSeekBars();
		calculate();

		// set click listener for canopy list button
		Button canopyListButton = (Button) findViewById(R.id.buttonShowCanopyList);
		canopyListButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent i = new Intent(getBaseContext(),
						CanopyListActivity.class);
				i.putExtra("CATEGORY", category);
				i.putExtra("TOTALJUMPS",
						((SeekBar) findViewById(R.id.seekBarTotalJumps))
								.getProgress());
				i.putExtra("JUMPSLAST12MONTHS",
						((SeekBar) findViewById(R.id.seekBarJumpsLast12Months))
								.getProgress());
				i.putExtra(
						"WEIGHT",
						((SeekBar) findViewById(R.id.seekBarWeight))
								.getProgress() + WEIGHT_MIN);
				i.putExtra("MINAREA", minArea);
				startActivity(i);
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_calculate, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_resetbeginner:
			((SeekBar) findViewById(R.id.seekBarWeight))
					.setProgress(WEIGHT_DEFAULT - WEIGHT_MIN);
			((SeekBar) findViewById(R.id.seekBarTotalJumps)).setProgress(5);
			((SeekBar) findViewById(R.id.seekBarJumpsLast12Months))
					.setProgress(5);
			return true;
		case R.id.menu_resetnormal:
			((SeekBar) findViewById(R.id.seekBarWeight))
					.setProgress(WEIGHT_DEFAULT - WEIGHT_MIN);
			((SeekBar) findViewById(R.id.seekBarTotalJumps))
					.setProgress(TOTALJUMPS_DEFAULT);
			((SeekBar) findViewById(R.id.seekBarJumpsLast12Months))
					.setProgress(JUMPS_LAST_12_MONTHS_DEFAULT);
			return true;
		case R.id.menu_resetexpert:
			((SeekBar) findViewById(R.id.seekBarWeight))
					.setProgress(WEIGHT_DEFAULT - WEIGHT_MIN);
			((SeekBar) findViewById(R.id.seekBarTotalJumps)).setProgress(1200);
			((SeekBar) findViewById(R.id.seekBarJumpsLast12Months))
					.setProgress(200);
			return true;
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void initSeekBars() {
		// weight seek bar
		SeekBar sbWeight = (SeekBar) findViewById(R.id.seekBarWeight);
		int weightInKg = prefs.getInt(SETTING_WEIGHT, WEIGHT_DEFAULT);
		sbWeight.setMax(WEIGHT_MAX - WEIGHT_MIN);
		sbWeight.setProgress(weightInKg - WEIGHT_MIN);
		setWeightSettingText(weightInKg);
		sbWeight.setOnSeekBarChangeListener(seekBarChangeListenerWeight);
		setPlusMinButtonListeners(sbWeight, R.id.buttonWeightMin,
				R.id.buttonWeightPlus);

		// total jumps seek bar
		SeekBar sbTotalJumps = (SeekBar) findViewById(R.id.seekBarTotalJumps);
		int totalJumps = prefs.getInt(SETTING_TOTAL_JUMPS, TOTALJUMPS_DEFAULT);
		sbTotalJumps.setMax(TOTALJUMPS_MAX);
		sbTotalJumps.setProgress(totalJumps);
		setTotalJumpsSettingText(totalJumps);
		sbTotalJumps
				.setOnSeekBarChangeListener(seekBarChangeListenerTotalJumps);
		setPlusMinButtonListeners(sbWeight, R.id.buttonTotalJumpsMin,
				R.id.buttonTotalJumpsPlus);

		// jumps last 12 months seek bar
		SeekBar sbJumpsLast12Months = (SeekBar) findViewById(R.id.seekBarJumpsLast12Months);
		int jumpsLast12Months = prefs.getInt(SETTING_JUMPS_LAST_12_MONTHS,
				JUMPS_LAST_12_MONTHS_DEFAULT);
		sbJumpsLast12Months.setMax(JUMPS_LAST_12_MONTHS_MAX);
		sbJumpsLast12Months.setProgress(jumpsLast12Months);
		setJumpsLast12MonthsSettingText(jumpsLast12Months);
		sbJumpsLast12Months
				.setOnSeekBarChangeListener(seekBarChangeListenerJumpsLast12Months);
		setPlusMinButtonListeners(sbWeight, R.id.buttonJumpLast12MonthsMin,
				R.id.buttonJumpLast12MonthsPlus);

	}

	private void setPlusMinButtonListeners(SeekBar sb, int minButtonId,
			int plusButtonId) {
		Button minButton = (Button) findViewById(minButtonId);
		Button plusButton = (Button) findViewById(plusButtonId);
		minButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// find matching seekbar
				ViewGroup hList = (ViewGroup) v.getParent();
				if (hList.getChildCount() != 3)
					Log.e(LOG_TAG,
							"Incorrect number of children in seekbargroup");
				SeekBar sb = (SeekBar) hList.getChildAt(1);
				int progress = sb.getProgress();
				if (progress > 0)
					sb.setProgress(progress - 1);
			}
		});
		plusButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// find matching seekbar
				ViewGroup hList = (ViewGroup) v.getParent();
				if (hList.getChildCount() != 3)
					Log.e(LOG_TAG,
							"Incorrect number of children in seekbargroup");
				SeekBar sb = (SeekBar) hList.getChildAt(1);
				int progress = sb.getProgress();
				if (progress < sb.getMax())
					sb.setProgress(progress + 1);
			}
		});

	}

	private OnSeekBarChangeListener seekBarChangeListenerWeight = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			int weightInKg = progress + WEIGHT_MIN;
			savePreference(SETTING_WEIGHT, weightInKg);
			setWeightSettingText(weightInKg);
			calculate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	private OnSeekBarChangeListener seekBarChangeListenerTotalJumps = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			savePreference(SETTING_TOTAL_JUMPS, progress);
			setTotalJumpsSettingText(progress);
			// check to see if jumps in last 12 months in not higher
			SeekBar sbJumpsLast12Months = (SeekBar) findViewById(R.id.seekBarJumpsLast12Months);
			int jumpsLast12Months = sbJumpsLast12Months.getProgress();
			if (jumpsLast12Months > progress)
				sbJumpsLast12Months.setProgress(progress);
			calculate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	private OnSeekBarChangeListener seekBarChangeListenerJumpsLast12Months = new OnSeekBarChangeListener() {

		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			savePreference(SETTING_JUMPS_LAST_12_MONTHS, progress);
			setJumpsLast12MonthsSettingText(progress);
			// check to see if jumps in last 12 months in not higher
			SeekBar sbTotalJumps = (SeekBar) findViewById(R.id.seekBarTotalJumps);
			int totalJumps = sbTotalJumps.getProgress();
			if (totalJumps < progress)
				sbTotalJumps.setProgress(progress);
			calculate();
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub

		}
	};

	/***
	 * Saves an (integer) preference to the global prefs var.
	 * 
	 * @param preferenceName
	 *            the name of the preference
	 * @param value
	 *            the (new) value of the preference
	 */
	private void savePreference(String preferenceName, int value) {
		Editor e = prefs.edit();
		e.putInt(preferenceName, value);
		e.commit();
	}

	private void setWeightSettingText(int weightInKg) {
		int weightInLbs = Calculation.kgToLbs(weightInKg);
		TextView tvWeight = (TextView) findViewById(R.id.textViewWeightLabel);
		String weightLabel = getString(R.string.weightLabel);
		String weightFormat = getString(R.string.weightSetting);
		tvWeight.setText(weightLabel
				+ String.format(weightFormat, weightInKg, weightInLbs));

		// fill the wingload table
		int column = 0;
		TextView area1 = (TextView) findViewById(R.id.textViewArea1);
		TextView wingLoad1 = (TextView) findViewById(R.id.textViewWingLoad1);
		fillWingLoadTableColumn(column++, weightInLbs, area1, wingLoad1);
		TextView area2 = (TextView) findViewById(R.id.textViewArea2);
		TextView wingLoad2 = (TextView) findViewById(R.id.textViewWingLoad2);
		fillWingLoadTableColumn(column++, weightInLbs, area2, wingLoad2);
		TextView area3 = (TextView) findViewById(R.id.textViewArea3);
		TextView wingLoad3 = (TextView) findViewById(R.id.textViewWingLoad3);
		fillWingLoadTableColumn(column++, weightInLbs, area3, wingLoad3);
		TextView area4 = (TextView) findViewById(R.id.textViewArea4);
		TextView wingLoad4 = (TextView) findViewById(R.id.textViewWingLoad4);
		fillWingLoadTableColumn(column++, weightInLbs, area4, wingLoad4);
		TextView area5 = (TextView) findViewById(R.id.textViewArea5);
		TextView wingLoad5 = (TextView) findViewById(R.id.textViewWingLoad5);
		fillWingLoadTableColumn(column++, weightInLbs, area5, wingLoad5);
		TextView area6 = (TextView) findViewById(R.id.textViewArea6);
		TextView wingLoad6 = (TextView) findViewById(R.id.textViewWingLoad6);
		fillWingLoadTableColumn(column++, weightInLbs, area6, wingLoad6);

	}

	private void fillWingLoadTableColumn(int column, int weightInLbs,
			TextView tvArea, TextView tvWingLoad) {
		// TODO Auto-generated method stub
		int area = WINGLOAD_FIRST_COL + column * WINGLOAD_STEP;
		double wingload = (double) weightInLbs / (double) area;
		tvArea.setText(String.format(" %d ", area));
		tvWingLoad.setText(String.format(" %.2f ", wingload));
	}

	private void setTotalJumpsSettingText(int totalJumps) {
		TextView tvTotalJumps = (TextView) findViewById(R.id.textViewTotalJumpsLabel);
		String totalJumpsLabel = getString(R.string.totalJumpsLabel);
		String totalJumpsFormat = getString(R.string.totalJumpsSetting);
		String orMoreText = "";
		if (totalJumps > TOTALJUMPS_LASTGROUP)
			orMoreText = getString(R.string.ormore);
		tvTotalJumps.setText(totalJumpsLabel
				+ String.format(totalJumpsFormat, totalJumps, orMoreText));
	}

	private void setJumpsLast12MonthsSettingText(int jumps) {
		TextView tvJumpsLast12Months = (TextView) findViewById(R.id.textViewJumpsLast12MonthsLabel);
		String jumpsLast12MonthsLabel = getString(R.string.jumpsLast12MonthsLabel);
		String jumpsLast12MonthsFormat = getString(R.string.jumpsLast12MonthsSetting);
		String orMoreText = "";
		if (jumps > JUMPS_LAST_12_MONTHS_LASTGROUP)
			orMoreText = getString(R.string.ormore);
		tvJumpsLast12Months.setText(jumpsLast12MonthsLabel
				+ String.format(jumpsLast12MonthsFormat, jumps, orMoreText));
	}

	/**
	 * Calculate the current category, based on weight, total jumps and jumps
	 * last year
	 */
	private void calculate() {
		// get weight and set text
		SeekBar sbWeight = (SeekBar) findViewById(R.id.seekBarWeight);
		int weightInKg = sbWeight.getProgress() + WEIGHT_MIN;

		SeekBar sbTotalJumps = (SeekBar) findViewById(R.id.seekBarTotalJumps);
		int totalJumps = sbTotalJumps.getProgress();

		SeekBar sbJumpsLast12Months = (SeekBar) findViewById(R.id.seekBarJumpsLast12Months);
		int jumpsLast12Months = sbJumpsLast12Months.getProgress();

		int jumperCategory = Calculation.jumperCategory(totalJumps,
				jumpsLast12Months);

		// now decide on minArea and maxWingload
		int minArea = Calculation.minArea(jumperCategory, weightInKg);

		// only update screen if there actually is a change, so speedbars respond quickly
		if (this.category != jumperCategory || this.minArea != minArea) {
			TextView tvJumperCategory = (TextView) findViewById(R.id.textViewJumperCategory);
			String jumperCatFormat = getString(R.string.categorySetting);
			tvJumperCategory.setText(String.format(jumperCatFormat,
					jumperCategory));

			TextView tvJumperCategoryDescription = (TextView) findViewById(R.id.textViewJumperCategoryDescription);
			String jumperCatDescriptionFormat = getString(R.string.categorySettingDescription);
			String jumperCategories[] = getResources().getStringArray(
					R.array.jumperCategories);
			tvJumperCategoryDescription.setText(String.format(
					jumperCatDescriptionFormat,
					jumperCategories[jumperCategory]));

			TextView tvCanopyCategory = (TextView) findViewById(R.id.textViewCanopyCategory);
			String canopyCatFormat = getString(R.string.canopySetting);
			String canopyCategories[] = getResources().getStringArray(
					R.array.canopyCategories);
			tvCanopyCategory.setText(String.format(canopyCatFormat,
					jumperCategory, canopyCategories[jumperCategory]));

			TextView tvCanopyAdvise = (TextView) findViewById(R.id.textViewCanopyAdvise);
			String canopyAdviseFormat = getString(R.string.canopyAdvise);
			tvCanopyAdvise.setText(String.format(canopyAdviseFormat,
					jumperCategory, minArea));

			// save globally, to pass on in buttonClick
			this.category = jumperCategory;
			this.minArea = minArea;
		}
		// get total jumps and set text
		// get jumps last 12 months and set text
		// calculate category and set text
	}
}
