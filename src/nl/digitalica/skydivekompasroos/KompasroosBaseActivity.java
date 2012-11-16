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

	// class variables
	SharedPreferences prefs;

	// shared variables, to pass the configured settings between activities
	int category = 0;
	int minArea = MINAREAUNKOWN;

}
