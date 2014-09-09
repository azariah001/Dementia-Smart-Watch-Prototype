package com.illusivemen.setting;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

public class ShowSettings extends Activity {
	
	// Sets the current view onto the loaded Setting view
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FragmentManager settingsFragMan = getFragmentManager();
		FragmentTransaction settingsFragTrans = settingsFragMan.beginTransaction();
		settingsFragTrans.replace(android.R.id.content, new Settings());
		settingsFragTrans.commit();
	}
}
