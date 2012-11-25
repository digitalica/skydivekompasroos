package nl.digitalica.skydivekompasroos;

import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class CanopyDetailsActivity extends KompasroosBaseActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_canopydetails);

		Bundle extras = getIntent().getExtras();
		String canopyKey = extras.getString(CANOPYKEYEXTRA);
		List<Canopy> thisCanopy = Canopy.getCanopiesInList(canopyKey, this);
		HashMap<String, Manufacturer> manufacturers = Manufacturer
				.getManufacturerHash(CanopyDetailsActivity.this);

		if (thisCanopy.size() != 1)
			Log.e(LOG_TAG, "Onjuist aantal op basis van key " + canopyKey);

		Canopy canopy = thisCanopy.get(0);
		Manufacturer manufacturer = manufacturers.get(canopy.manufacturer);

		String url = manufacturer.url;
		if (canopy.url != "" && canopy.url != null)
			url = canopy.url; // if we have a canopy url use that.

		TextView tvName = (TextView) findViewById(R.id.textViewNameText);
		TextView tvCategory = (TextView) findViewById(R.id.textViewCategoryText);
		TextView tvExperience = (TextView) findViewById(R.id.textViewExperienceText);
		TextView tvCells = (TextView) findViewById(R.id.textViewCellsText);
		TextView tvSizes = (TextView) findViewById(R.id.textViewSizesText);
		TextView tvUrl = (TextView) findViewById(R.id.textViewUrlText);
		TextView tvManufacturer = (TextView) findViewById(R.id.textViewManufacturerText);
		TextView tvManufacturerCountry = (TextView) findViewById(R.id.textViewManufacturerCountryText);
		TextView tvRemarks = (TextView) findViewById(R.id.textViewRemarksText);

		tvName.setText(canopy.name);
		tvName.setBackgroundDrawable(backgroundDrawableForAcceptance(canopy.acceptablility(currentMaxCategory, currentWeight)));

		tvCategory.setText(Integer.toString(canopy.category));
		String[] jumperCategories = getResources().getStringArray(
				R.array.jumperCategories);
		tvExperience.setText(jumperCategories[canopy.category]);
		tvCells.setText(canopy.cells);
		String sizes = "";
		if (canopy.minSize != "" && canopy.minSize != null)
			if (canopy.maxSize != "" && canopy.maxSize != null) {
				// TODO: change to format string in strings for translation
				sizes = canopy.minSize + " tot " + canopy.maxSize + " sqft";
			}
		tvSizes.setText(sizes);
		tvUrl.setText(url);
		tvManufacturer.setText(canopy.manufacturer);
		tvManufacturerCountry.setText(manufacturer.countryFullName());
		StringBuilder remarks = new StringBuilder();
		if (canopy.remarks != "" && canopy.remarks != null) {
			remarks.append(canopy.remarks);
			remarks.append(System.getProperty("line.separator"));
		}
		if (manufacturer.remarks != "" && manufacturer.remarks != null)
			remarks.append(manufacturer.remarks);
		tvRemarks.setText(remarks.toString());
	}
}