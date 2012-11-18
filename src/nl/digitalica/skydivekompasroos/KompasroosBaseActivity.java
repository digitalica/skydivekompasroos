package nl.digitalica.skydivekompasroos;

import android.app.Activity;
import android.content.SharedPreferences;

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
	static int currentJumpsInLast12Months = 0;
	static int currentWeight = 0;

	static int category = 0;
	static int minArea = MINAREAUNKOWN;

}
