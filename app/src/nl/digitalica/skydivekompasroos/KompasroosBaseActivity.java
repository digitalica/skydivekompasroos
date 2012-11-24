package nl.digitalica.skydivekompasroos;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;

/***
 * Base class for kompasroos activities
 * 
 * @author robbert
 * 
 */
public class KompasroosBaseActivity extends Activity {

	final static String KOMPASROOSPREFS = "Skydive_Kompasroos_Preferences";
	final static String LOG_TAG = "SkydiveKompasRoos";

	final static int MINAREAUNKOWN = 999;
	final static int MINAREAUNLIMITED = 0;

	final static String CANOPYKEYEXTRA = "CANOPYKEY";

	// class variables
	SharedPreferences prefs;

	// shared variables, to pass the configured settings between activities
	static int currentTotalJumps = 0;
	static int currentJumpsLast12Months = 0;
	static int currentWeight = 0;
	static int currentMaxCategory = 0;
	static int currentMinArea = MINAREAUNKOWN;

	Drawable backgroundDrawableForAcceptance(int acceptability) {
		Drawable background = null;
		if (acceptability == Canopy.ACCEPTABLE)
			background = getResources()
					.getDrawable(R.drawable.canopyacceptable);
		else if (acceptability == Canopy.NEEDEDSIZENOTAVAILABLE)
			background = getResources().getDrawable(
					R.drawable.canopyneededsizenotavailable);
		else if (acceptability == Canopy.CATEGORYTOOHIGH)
			background = getResources().getDrawable(
					R.drawable.canopycategorytoohigh);
		return background;
	}

}
