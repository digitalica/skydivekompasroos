package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.xmlpull.v1.XmlPullParserException;

import android.app.Activity;
import android.content.Intent;
import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TableLayout;
import android.widget.TableRow;
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
		TextView tvUrl = (TextView) findViewById(R.id.textViewUrlText);
		TextView tvManufacturer = (TextView) findViewById(R.id.textViewManufacturerText);

		tvName.setText(canopy.name);
		tvCategory.setText(Integer.toString(canopy.category));
		tvUrl.setText(url);
		tvManufacturer.setText(canopy.manufacturer);
	}
}
