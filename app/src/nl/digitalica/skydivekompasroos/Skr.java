package nl.digitalica.skydivekompasroos;

import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import nl.digitalica.skydivekompasroos.CanopyBase.AcceptabilityEnum;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;

public class Skr {

	public final static String KOMPASROOSPREFS = "Skydive_Kompasroos_Preferences";
	public final static String LOG_TAG = "SkydiveKompasRoos";
	
	public final static int MINAREAUNKOWN = 999;
	public final static int MINAREAUNLIMITED = 0;

	public final static String CANOPYIDEXTRA = "CANOPYKEY";

	public final static String SPECIFICCANOPYID_KEY = "SpecificCanopyId";
	

	// shared variables, to pass the configured settings between activities
	public static int currentTotalJumps = 0;
	public static int currentJumpsLast12Months = 0;
	public static int currentWeight = 0;
	public static int currentMaxCategory = 0;
	public static int currentMinArea = MINAREAUNKOWN;

	// namen voor de verschillende settings
	public final static String SETTING_WEIGHT = "Weight"; // int
	public final static String SETTING_TOTAL_JUMPS = "TotalJumps"; // int
	public final static String SETTING_JUMPS_LAST_12_MONTHS = "JumpsLast12Months"; // int
	public final static String SETTING_OWN_WEIGHT = "OwnWeight";
	public final static String SETTING_OWN_TOTAL_JUMPS = "OwnTotalJumps";
	public final static String SETTING_OWN_LAST_12_MONTHS = "OwnJumpsLast12Months";
	public final static String SETTING_FRIEND_WEIGHT = "FriendWeight";
	public final static String SETTING_FRIEND_TOTAL_JUMPS = "FriendTotalJumps";
	public final static String SETTING_FRIEND_LAST_12_MONTHS = "FriendJumpsLast12Months";

	public static final String SETTING_SORTING = "Sorting";
	public static final String SETTING_FILTER_TYPE = "FilterType";
	public static final String SETTING_SORTING_FILTER_DAYNR = "SortingFilterTime";

	
	/***
	 * Find the compile Date Time of the app
	 * 
	 * @return
	 * @throws NameNotFoundException
	 * @throws IOException
	 */
	public static long getCompileDateTime(Context ctx) throws NameNotFoundException, IOException {
		ApplicationInfo ai = ctx.getPackageManager().getApplicationInfo(
				ctx.getPackageName(), 0);
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
	public static void savePreference(Context ctx, String preferenceName, int value) {
		SharedPreferences prefs = ctx.getSharedPreferences(Skr.KOMPASROOSPREFS,
				Context.MODE_PRIVATE);
		Editor e = prefs.edit();
		e.putInt(preferenceName, value);
		e.commit();
	}


	/***
	 * Returns a suitable background based on the acceptability of a canopy
	 * 
	 * TODO: move to canopytype class (?)
	 * 
	 * @param acceptability
	 * @return
	 */
	public static Drawable backgroundForAcceptance(Context ctx, AcceptabilityEnum acceptability) {
		Drawable background = null;
		if (acceptability == AcceptabilityEnum.ACCEPTABLE)
			background = ctx.getResources()
					.getDrawable(R.drawable.canopyacceptable);
		else if (acceptability == AcceptabilityEnum.NEEDEDSIZENOTAVAILABLE)
			background = ctx.getResources().getDrawable(
					R.drawable.canopyneededsizenotavailable);
		else if (acceptability == AcceptabilityEnum.CATEGORYTOOHIGH)
			background = ctx.getResources().getDrawable(
					R.drawable.canopycategorytoohigh);
		return background;
	}


	
}
