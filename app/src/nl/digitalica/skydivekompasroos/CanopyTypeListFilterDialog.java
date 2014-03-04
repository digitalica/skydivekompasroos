package nl.digitalica.skydivekompasroos;

import nl.digitalica.skydivekompasroos.CanopyTypeListFragment.FilterEnum;
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

public class CanopyTypeListFilterDialog extends DialogFragment {

    public interface FilterDialogListener {
    	CanopyTypeListFragment.FilterEnum getFilterType();
    	void setFilterType(CanopyTypeListFragment.FilterEnum filterType);
    }
	
	
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.filter_dialog,
				(ViewGroup) getActivity().findViewById(R.id.root));

		// check the correct radio button in group
		RadioButton radioToCheck = null;
		FilterDialogListener fragment = (FilterDialogListener) getFragmentManager().findFragmentByTag(CanopyTypeListFragment.TAG);
		switch (fragment.getFilterType()) {
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

		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setView(layout);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// We forcefully dismiss and remove the Dialog, so it
						// cannot be used again (no cached info)
						// getActivity().removeDialog(SORT_DIALOG_ID);
						dismiss();
					}
				});

		RadioGroup rg = (RadioGroup) layout
				.findViewById(R.id.radioGroupFilterOptions);
		rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

			public void onCheckedChanged(RadioGroup group, int checkedId) {
				int checkButtonId = group.getCheckedRadioButtonId();
				FilterDialogListener fragment = (FilterDialogListener) getFragmentManager().findFragmentByTag(CanopyTypeListFragment.TAG);
				switch (checkButtonId) {
				case R.id.radioButtonCommon:
					//CanopyTypeListFragment.currentFilterType = FilterEnum.ONLYCOMMON;
					fragment.setFilterType(FilterEnum.ONLYCOMMON);
					break;
				case R.id.radioButtonCommonAround:
					//CanopyTypeListFragment.currentFilterType = FilterEnum.COMMONAROUNDMAX;
					fragment.setFilterType(FilterEnum.COMMONAROUNDMAX);
					break;
				case R.id.radioButtonAll:
					//CanopyTypeListFragment.currentFilterType = FilterEnum.ALL;
					fragment.setFilterType(FilterEnum.ALL);
					break;
				}

				// CanopyTypeListFragment.this.removeDialog(FILTER_DIALOG_ID);
				dismiss();
			}
		});

		return builder.create();
	}
}