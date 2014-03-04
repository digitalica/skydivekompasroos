package nl.digitalica.skydivekompasroos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.SeekBar;

public class CalculateJumpsLast12MonthsDialog extends DialogFragment {

	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()
				.getApplicationContext());

		builder.setTitle(getString(R.string.numberOfJumpsLast12MonthsDialogTitle));
		builder.setMessage(String.format(
				getString(R.string.enterNumberOfJumpsLast12MonthsFormat),
				CalculateFragment.JUMPS_LAST_12_MONTHS_MAX));

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity()
				.getApplicationContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String valueText = input.getText().toString();
				int value = Integer.parseInt(valueText);
				if (value >= 0 && value <= CalculateFragment.JUMPS_LAST_12_MONTHS_MAX) {
					SeekBar sb = (SeekBar) getView().findViewById(
							R.id.seekBarJumpsLast12Months);
					sb.setProgress(value);
				}
				// make sure it will be initialized next time...
				getActivity().removeDialog(CalculateFragment.JUMPS_LAST_12_MONTHS_DIALOG_ID);
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// make sure it will be initialized next time...
						getActivity().removeDialog(
								CalculateFragment.JUMPS_LAST_12_MONTHS_DIALOG_ID);
					}
				});

		return builder.create();

	}
	
}
