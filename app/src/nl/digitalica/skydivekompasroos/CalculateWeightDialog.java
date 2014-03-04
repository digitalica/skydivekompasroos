package nl.digitalica.skydivekompasroos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.SeekBar;

public class CalculateWeightDialog  extends DialogFragment {

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()
				.getApplicationContext());

		builder.setTitle(getString(R.string.weightDialogTitle));
		builder.setMessage(String.format(getString(R.string.enterWeightFormat),
				CalculateFragment.WEIGHT_MIN, CalculateFragment.WEIGHT_MAX));

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity()
				.getApplicationContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String valueText = input.getText().toString();
				int value = Integer.parseInt(valueText);
				if (value >= CalculateFragment.WEIGHT_MIN && value <= CalculateFragment.WEIGHT_MAX) {
					SeekBar sb = (SeekBar) getView().findViewById(
							R.id.seekBarWeight);
					sb.setProgress(value - CalculateFragment.WEIGHT_MIN);
				}
				// make sure it will be initialized next time...
				getActivity().removeDialog(CalculateFragment.WEIGHT_DIALOG_ID);
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// make sure it will be initialized next time...
						getActivity().removeDialog(CalculateFragment.WEIGHT_DIALOG_ID);
					}
				});

		return builder.create();
	}
}
