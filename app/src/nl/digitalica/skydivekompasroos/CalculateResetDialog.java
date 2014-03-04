package nl.digitalica.skydivekompasroos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CalculateResetDialog extends DialogFragment {

    public interface ResetDialogListener {
        void onFinishResetDialog(int weight, int totalJumps, int jumpsLast12Months);
    }

	
	/***
	 * The dialog to reset the settings. Settings can be reset to beginner,
	 * intermediate or pro or to saved users own or friend settings.
	 * 
	 * @return
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v0 = getView();
		View v1 = v0.getRootView();
		View v2 = v1.findViewById(R.id.root);
		View layout = inflater.inflate(R.layout.reset_dialog,
				(ViewGroup) v2);
		Button asBeginner = (Button) layout.findViewById(R.id.buttonBeginner);
		Button asIntermediate = (Button) layout
				.findViewById(R.id.buttonIntermediate);
		Button asSkyGod = (Button) layout.findViewById(R.id.buttonSkyGod);
		Button asFriend = (Button) layout.findViewById(R.id.buttonFriend);
		Button asOwn = (Button) layout.findViewById(R.id.buttonOwn);
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity()
				.getApplicationContext());
		builder.setView(layout);
		builder.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int whichButton) {
						// We forcefully dismiss and remove the Dialog, so it
						// cannot be used again (no cached info)
						// getActivity().removeDialog(RESET_DIALOG_ID);
						dismiss();
					}
				});
		asBeginner.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResetDialogListener fragment = (ResetDialogListener) getFragmentManager().findFragmentByTag(CalculateFragment.TAG);
				fragment.onFinishResetDialog(CalculateFragment.WEIGHT_DEFAULT - CalculateFragment.WEIGHT_MIN, 5, 5);
				// getActivity().removeDialog(RESET_DIALOG_ID);
				dismiss();
			}
		});
		asIntermediate.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResetDialogListener fragment = (ResetDialogListener) getFragmentManager().findFragmentByTag(CalculateFragment.TAG);
				fragment.onFinishResetDialog(CalculateFragment.WEIGHT_DEFAULT - CalculateFragment.WEIGHT_MIN, CalculateFragment.TOTALJUMPS_DEFAULT,
						CalculateFragment.JUMPS_LAST_12_MONTHS_DEFAULT);
				// getActivity().removeDialog(RESET_DIALOG_ID);
				dismiss();
			}
		});
		asSkyGod.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				ResetDialogListener fragment = (ResetDialogListener) getFragmentManager().findFragmentByTag(CalculateFragment.TAG);
				fragment.onFinishResetDialog(CalculateFragment.WEIGHT_DEFAULT - CalculateFragment.WEIGHT_MIN, 1200, 200);
				// getActivity().removeDialog(RESET_DIALOG_ID);
				dismiss();
			}

		});
		asFriend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getSharedPreferences(
						Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
				int weight = prefs.getInt(Skr.SETTING_FRIEND_WEIGHT,
						CalculateFragment.WEIGHT_DEFAULT);
				int totalJumps = prefs.getInt(Skr.SETTING_FRIEND_TOTAL_JUMPS,
						CalculateFragment.TOTALJUMPS_DEFAULT);
				int jumpsLastMonth = prefs.getInt(
						Skr.SETTING_FRIEND_LAST_12_MONTHS,
						CalculateFragment.JUMPS_LAST_12_MONTHS_DEFAULT);
				ResetDialogListener fragment = (ResetDialogListener) getFragmentManager().findFragmentByTag(CalculateFragment.TAG);
				fragment.onFinishResetDialog(weight - CalculateFragment.WEIGHT_MIN, totalJumps, jumpsLastMonth);
				// getActivity().removeDialog(RESET_DIALOG_ID);
				dismiss();
			}
		});
		asOwn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getSharedPreferences(
						Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
				int weight = prefs.getInt(Skr.SETTING_OWN_WEIGHT,
						CalculateFragment.WEIGHT_DEFAULT);
				int totalJumps = prefs.getInt(Skr.SETTING_OWN_TOTAL_JUMPS,
						CalculateFragment.TOTALJUMPS_DEFAULT);
				int jumpsLastMonth = prefs.getInt(
						Skr.SETTING_OWN_LAST_12_MONTHS,
						CalculateFragment.JUMPS_LAST_12_MONTHS_DEFAULT);
				ResetDialogListener fragment = (ResetDialogListener) getFragmentManager().findFragmentByTag(CalculateFragment.TAG);
				fragment.onFinishResetDialog(weight - CalculateFragment.WEIGHT_MIN, totalJumps, jumpsLastMonth);
				// getActivity().removeDialog(RESET_DIALOG_ID);
				dismiss();
			}
		});
		return builder.create();
	}

}
