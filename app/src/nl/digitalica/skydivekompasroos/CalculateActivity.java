package nl.digitalica.skydivekompasroos;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CalculateActivity extends KompasroosBaseActivity {

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_calculate);
		
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction fragmentTransaction = fragmentManager
				.beginTransaction();
		CalculateFragment fragment = new CalculateFragment();
		fragmentTransaction.add(R.id.fragment_container_calculate, fragment);
		fragmentTransaction.commit();
		
	}

}
