package nl.digitalica.skydivekompasroos;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public class CanopyTypeListActivity  extends KompasroosBaseActivity {

		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_canopytypelist);
			
			FragmentManager fragmentManager = getSupportFragmentManager();
			FragmentTransaction fragmentTransaction = fragmentManager
					.beginTransaction();
			CanopyTypeListFragment fragment = new CanopyTypeListFragment();
			fragmentTransaction.add(R.id.fragment_container_canopytypelist, fragment, CanopyTypeListFragment.TAG);
			fragmentTransaction.commit();
			
		}

	}
