package nl.digitalica.skydivekompasroos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends KompasroosBaseActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);

		TextView aboutText = (TextView) findViewById(R.id.textViewAbout);

		// read text file
		InputStream iFile = getResources().openRawResource(R.raw.about);
		String aboutString = "";
		try {
			aboutString = inputStreamToString(iFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// add version number and compile date/time
		String mVersionNumber = "?";
		try {
			String pkg = getPackageName();
			mVersionNumber = getPackageManager().getPackageInfo(pkg, 0).versionName;
		} catch (Exception e) {
		}

		String compileDate = "";
		try {
			ApplicationInfo ai = getPackageManager().getApplicationInfo(
					getPackageName(), 0);
			ZipFile zf = new ZipFile(ai.sourceDir);
			ZipEntry ze = zf.getEntry("classes.dex");
			long time = ze.getTime();
			compileDate = SimpleDateFormat.getInstance().format(
					new java.util.Date(time));

		} catch (Exception e) {
		}
		if (!compileDate.equals(""))
			compileDate = " (" + compileDate + ")";

		aboutString += "\n\nVersion: " + mVersionNumber + compileDate;

		// put result in testview
		aboutText.setText(aboutString);
	}

	private String inputStreamToString(InputStream iFile) throws IOException {
		// TODO Auto-generated method stub
		BufferedReader r = new BufferedReader(new InputStreamReader(iFile));
		StringBuilder total = new StringBuilder();
		String line;
		while ((line = r.readLine()) != null) {
			total.append(line);
			total.append(System.getProperty("line.separator"));
		}

		return total.toString();
	}

}
