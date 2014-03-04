package nl.digitalica.skydivekompasroos;
import nl.digitalica.skydivekompasroos.R;
import nl.digitalica.skydivekompasroos.Skr;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class CalculateSaveDialog extends DialogFragment {

	/***
	 * The save dialog to allow saving the current settings. They can be saved
	 * as 'own' or 'friend' settings.
	 * 
	 * @return
	 */
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.save_dialog,
				(ViewGroup) getView().findViewById(R.id.root));
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
						//getActivity().removeDialog(SAVE_DIALOG_ID);
						dismiss();
					}
				});
		asFriend.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getSharedPreferences(
						Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
				Editor e = prefs.edit();
				e.putInt(Skr.SETTING_FRIEND_TOTAL_JUMPS, Skr.currentTotalJumps);
				e.putInt(Skr.SETTING_FRIEND_LAST_12_MONTHS,
						Skr.currentJumpsLast12Months);
				e.putInt(Skr.SETTING_FRIEND_WEIGHT, Skr.currentWeight);
				e.commit();
				//getActivity().removeDialog(SAVE_DIALOG_ID);
				dismiss();
			}
		});
		asOwn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				SharedPreferences prefs = getActivity().getSharedPreferences(
						Skr.KOMPASROOSPREFS, Context.MODE_PRIVATE);
				Editor e = prefs.edit();
				e.putInt(Skr.SETTING_OWN_TOTAL_JUMPS, Skr.currentTotalJumps);
				e.putInt(Skr.SETTING_OWN_LAST_12_MONTHS,
						Skr.currentJumpsLast12Months);
				e.putInt(Skr.SETTING_OWN_WEIGHT, Skr.currentWeight);
				e.commit();
				//getActivity().removeDialog(SAVE_DIALOG_ID);
				dismiss();
			}
		});
		return builder.create();
	}

}
