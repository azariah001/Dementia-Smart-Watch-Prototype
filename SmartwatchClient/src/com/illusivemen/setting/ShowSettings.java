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
		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.replace(android.R.id.content, new Settings());
		ft.commit();
	}
}
