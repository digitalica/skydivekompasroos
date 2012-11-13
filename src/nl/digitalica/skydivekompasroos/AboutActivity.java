package nl.digitalica.skydivekompasroos;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class AboutActivity extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		
		TextView aboutText = (TextView)findViewById(R.id.textViewAbout);

		// read text file
		InputStream iFile = getResources().openRawResource(R.raw.about);
		String aboutString = "";
		try {
			aboutString = inputStreamToString(iFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
