package nl.digitalica.skydivekompasroos;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nl.digitalica.skydivekompasroos.CanopyTypeListActivity.SortingEnum;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.support.v4.app.NavUtils;

public class SpecificListEdit extends Activity {

	private String typesSpinner[];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_specific_list_edit);

		List<CanopyType> canopyTypes = CanopyType
				.getAllCanopyTypesInList(SpecificListEdit.this);

		Comparator<CanopyType> canopyComparator = new CanopyType.ComparatorByNameManufacturer();
		Collections.sort(canopyTypes, canopyComparator);

		typesSpinner = new String[canopyTypes.size()];
		int i = 0;
		for (CanopyType type : canopyTypes) {
			typesSpinner[i++] = type.name + " (" + type.manufacturerName + ")";
		}

		Spinner types = (Spinner) findViewById(R.id.spinnerType);
		ArrayAdapter adapter = new ArrayAdapter(this,
				android.R.layout.simple_spinner_item, typesSpinner);
		types.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_specific_list_edit, menu);
		return true;
	}

}
