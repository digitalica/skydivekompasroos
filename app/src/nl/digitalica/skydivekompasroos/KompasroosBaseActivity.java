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
import android.view.Window;

/***
 * Base class for kompasroos activities
 * 
 * @author robbert
 * 
 */
public class KompasroosBaseActivity extends android.support.v4.app.FragmentActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		Manufacturer.init(getApplicationContext());
		CanopyType.init(getApplicationContext());
	}

}
