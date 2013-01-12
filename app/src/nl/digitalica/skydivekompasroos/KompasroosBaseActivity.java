package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

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

	final static String CANOPYIDEXTRA = "CANOPYKEY";

	final static String SPECIFICCANOPYID_KEY = "SpecificCanopyId";
	
	// class variables
	SharedPreferences prefs;

	// shared variables, to pass the configured settings between activities
	static int currentTotalJumps = 0;
	static int currentJumpsLast12Months = 0;
	static int currentWeight = 0;
	static int currentMaxCategory = 0;
	static int currentMinArea = MINAREAUNKOWN;

	// namen voor de verschillende settings
	final static String SETTING_WEIGHT = "Weight"; // int
	final static String SETTING_TOTAL_JUMPS = "TotalJumps"; // int
	final static String SETTING_JUMPS_LAST_12_MONTHS = "JumpsLast12Months"; // int
	final static String SETTING_OWN_WEIGHT = "OwnWeight";
	final static String SETTING_OWN_TOTAL_JUMPS = "OwnTotalJumps";
	final static String SETTING_OWN_LAST_12_MONTHS = "OwnJumpsLast12Months";
	final static String SETTING_FRIEND_WEIGHT = "FriendWeight";
	final static String SETTING_FRIEND_TOTAL_JUMPS = "FriendTotalJumps";
	final static String SETTING_FRIEND_LAST_12_MONTHS = "FriendJumpsLast12Months";

	static final String SETTING_SORTING = "Sorting";
	static final String SETTING_FILTER_TYPE = "FilterType";
	static final String SETTING_SORTING_FILTER_DAYNR = "SortingFilterTime";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (prefs == null)
			prefs = getSharedPreferences(KOMPASROOSPREFS, Context.MODE_PRIVATE);
	}

	/***
	 * Returns a suitable background based on the acceptablity of a canopy
	 * 
	 * TODO: move to canopy class
	 * 
	 * @param acceptability
	 * @return
	 */
	Drawable backgroundDrawableForAcceptance(AcceptabilityEnum acceptability) {
		Drawable background = null;
		if (acceptability == AcceptabilityEnum.ACCEPTABLE)
			background = getResources()
					.getDrawable(R.drawable.canopyacceptable);
		else if (acceptability == AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE)
			background = getResources().getDrawable(
					R.drawable.canopyneededsizenotavailable);
		else if (acceptability == AcceptabilityEnum.CATEGORYTOOHIGH)
			background = getResources().getDrawable(
					R.drawable.canopycategorytoohigh);
		return background;
	}

	/***
	 * Find the compile Date Time of the app
	 * 
	 * @return
	 * @throws NameNotFoundException
	 * @throws IOException
	 */
	public long getCompileDateTime() throws NameNotFoundException, IOException {
		ApplicationInfo ai = getPackageManager().getApplicationInfo(
				getPackageName(), 0);
		ZipFile zf = new ZipFile(ai.sourceDir);
		ZipEntry ze = zf.getEntry("classes.dex");
		long time = ze.getTime();
		return time;
	}

	/***
	 * Saves an (integer) preference to the global prefs var.
	 * 
	 * @param preferenceName
	 *            the name of the preference
	 * @param value
	 *            the (new) value of the preference
	 */
	protected void savePreference(String preferenceName, int value) {
		Editor e = prefs.edit();
		e.putInt(preferenceName, value);
		e.commit();
	}

}
