package nl.digitalica.skydivekompasroos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.InputType;
import android.widget.EditText;
import android.widget.SeekBar;

public class CalculateTotalJumpsDialog extends DialogFragment {

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()
				.getApplicationContext());

		builder.setTitle(getString(R.string.totalNumberOfJumpsDialogTitle));
		builder.setMessage(String.format(
				getString(R.string.enterTotalNumberOfJumpsFormat),
				CalculateFragment.TOTALJUMPS_MAX));

		// Set an EditText view to get user input
		final EditText input = new EditText(getActivity()
				.getApplicationContext());
		input.setInputType(InputType.TYPE_CLASS_NUMBER);
		builder.setView(input);

		builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				String valueText = input.getText().toString();
				int value = Integer.parseInt(valueText);
				if (value >= 0 && value <= CalculateFragment.TOTALJUMPS_MAX) {
					SeekBar sb = (SeekBar) getView().findViewById(
							R.id.seekBarTotalJumps);
					sb.setProgress(value);
				}
				// make sure it will be initialized next time...
				// getActivity().removeDialog(TOTAL_JUMPS_DIALOG_ID);
				dismiss();
			}
		});

		builder.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// make sure it will be initialized next time...
						// getActivity().removeDialog(TOTAL_JUMPS_DIALOG_ID);
						dismiss();
					}
				});

		return builder.create();
	}

}
