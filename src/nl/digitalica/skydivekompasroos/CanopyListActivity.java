package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.TextAppearanceSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class CanopyListActivity extends KompasroosBaseActivity {

	// static, so it can be statically referenced from onClick...
	static StringBuilder skydiveKompasroosResult = new StringBuilder();

	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canopylist);

		TableLayout canopyTable = (TableLayout) findViewById(R.id.tablelayout_canopylist);

		List<Canopy> canopyList = Canopy
				.getAllCanopiesInList(CanopyListActivity.this);

		// sort the canopyList on type, name, manufacturer
		Collections.sort(canopyList, new Canopy.ComparatorByCategoryName());
		Collections.sort(canopyList, new Canopy.ComparatorByNameManufacturer());

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
		for (Canopy theCanopy : canopyList)
			insertCanopyRow(canopyTable, theCanopy, maxCategory);
		skydiveKompasroosResult.append(getString(R.string.shareresultfooter));

		// add onclick handler to button
		Button shareResultButton = (Button) findViewById(R.id.buttonShareResult);
		shareResultButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				shareResult();
			}

		});

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
		case R.id.menu_about:
			startActivity(new Intent(this, AboutActivity.class));
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void onCanopyRowClick(View v) {
		String canopyKey = v.getTag().toString();
		Intent i = new Intent(getBaseContext(),
				CanopyDetailsActivity.class);
		// TODO: remove extras as they will be in global vars...
		i.putExtra(CANOPYKEYEXTRA, canopyKey);
		startActivity(i);
	}

	private void insertCanopyRow(TableLayout canopyTable, Canopy theCanopy,
			int maxCategory) {

		Drawable box = getResources().getDrawable(R.drawable.box);
		LinearLayout hLayout = new LinearLayout(this);
		hLayout.setTag(theCanopy.key());
		hLayout.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onCanopyRowClick(v);
			}
		});
		hLayout.setOrientation(LinearLayout.HORIZONTAL);

		if (maxCategory >= theCanopy.category)
			hLayout.setBackgroundColor(getResources().getColor(
					R.color.Cat1GreenTransparent));
		else
			hLayout.setBackgroundColor(getResources().getColor(
					R.color.Cat1RedTransparent));

		// hLayout.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
		// LayoutParams.WRAP_CONTENT));

		TextView tvCategory = new TextView(this);
		tvCategory.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		// tvCategory.setBackgroundDrawable(box);
		tvCategory.setTextSize(getResources().getDimension(
				R.dimen.canopylistCategory));
		tvCategory.setText(Integer.toString(theCanopy.category));

		hLayout.addView(tvCategory);
		// hLayout.setBackgroundDrawable(box);

		LinearLayout vLayout = new LinearLayout(this);
		// vLayout.setBackgroundDrawable(box);
		vLayout.setOrientation(LinearLayout.VERTICAL);
		// vLayout.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,
		// LayoutParams.WRAP_CONTENT));

		TextView tvCanopyName = new TextView(this);
		tvCanopyName.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		tvCanopyName.setBackgroundDrawable(box);
		tvCanopyName.setTextSize(getResources().getDimension(
				R.dimen.canopylistCanopyName));
		tvCanopyName.setText(theCanopy.name);
		vLayout.addView(tvCanopyName);

		// TODO: add more details like cells and remarks here
		TextView tvCanopyDetails = new TextView(this);
		tvCanopyDetails.setTextSize(getResources().getDimension(
				R.dimen.canopylistCanopyDetails));
		tvCanopyDetails.setText(theCanopy.manufacturer);
		// tvCanopyDetails.setBackgroundDrawable(box);
		vLayout.addView(tvCanopyDetails);

		hLayout.addView(vLayout);

		// create text view and row
		TableRow row = new TableRow(this);
		row.addView(hLayout);

		// add row to table
		canopyTable.addView(row);

		// add row to text.
		skydiveKompasroosResult
				.append(theCanopy.name + " - " + theCanopy.manufacturer
						+ System.getProperty("line.separator"));

	}

}
