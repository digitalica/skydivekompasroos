package nl.digitalica.skydivekompasroos;

import nl.digitalica.skydivekompasroos.CanopyTypeListFilterDialog.FilterDialogListener;
import nl.digitalica.skydivekompasroos.CanopyTypeListFragment.SortingEnum;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class CanopyTypeListSortDialog extends DialogFragment {

    public interface SortDialogListener {
    	CanopyTypeListFragment.SortingEnum getSorting();
    	void setSorting(CanopyTypeListFragment.SortingEnum sorting);
    }

	
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.sort_dialog,
				(ViewGroup) getActivity().findViewById(R.id.root));

		// check the correct radio button in group
		RadioButton radioToCheck = null;
		SortDialogListener fragment = (SortDialogListener) getFragmentManager().findFragmentByTag(CanopyTypeListFragment.TAG);
		switch (fragment.getSorting()) {
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

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(layout);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// We forcefully dismiss and remove the Dialog, so it
						// cannot be used again (no cached info)
						//CanopyTypeListFragment.this.removeDialog(SORT_DIALOG_ID);
						dismiss();
					}
				});

		RadioGroup rg = (RadioGroup) layout
				.findViewById(R.id.radioGroupFilterOptions);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int checkButtonId = group.getCheckedRadioButtonId();
				SortDialogListener fragment = (SortDialogListener) getFragmentManager().findFragmentByTag(CanopyTypeListFragment.TAG);

				switch (checkButtonId) {
				case R.id.radioButtonByName:
					//currentSortingMethod = SortingEnum.SORTBYNAME;
					fragment.setSorting(SortingEnum.SORTBYNAME);
					break;
				case R.id.radioButtonByCategory:
					//currentSortingMethod = SortingEnum.SORTBYCATEGORY;
					fragment.setSorting(SortingEnum.SORTBYCATEGORY);
					break;
				case R.id.radioButtonByManufacturer:
					//currentSortingMethod = SortingEnum.SORTBYMANUFACTURER;
					fragment.setSorting(SortingEnum.SORTBYMANUFACTURER);
					break;
				}

				//getActivity().removeDialog(SORT_DIALOG_ID);
				dismiss();
			}
		});

		return builder.create();
	}
}